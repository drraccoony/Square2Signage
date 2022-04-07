package org.tyto.square.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.tyto.square.client.model.SquareOAuthRequest;
import org.tyto.square.client.model.SquareTokenData;
import org.tyto.square.configuration.SquareConfiguration;
import org.tyto.square.exception.SquareTokenRequestException;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.tyto.square.client.SquareApiHelper.*;

@Slf4j
@Component
public class SquareRestClient {

    private final SquareConfiguration config;
    private final WebClient client;
    private SquareTokenData tokenData;

    @Autowired
    public SquareRestClient(SquareConfiguration config) {
        this.config = config;
        this.client = WebClient.create();
        ScheduledExecutorService tokenRefresher = Executors.newSingleThreadScheduledExecutor();
        tokenRefresher.schedule(new TokenRefreshTask(this), 7, TimeUnit.DAYS);
    }

    private static class TokenRefreshTask implements Runnable {
        private final SquareRestClient client;

        public TokenRefreshTask(SquareRestClient client) {
            this.client = client;
        }

        @Override
        public void run() {
            this.client.refreshToken();
        }
    }

    private URI buildUri(String path) {
        return buildUri(path, Map.of());
    }

    private URI buildUri(String path, Map<String, List<String>> queryParameters) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .newInstance()
                .scheme(config.getEnvironment().getHttpScheme())
                .host(config.getEnvironment().getHost())
                .path(path);

        if (!CollectionUtils.isEmpty(queryParameters)) {
            builder.queryParams(new MultiValueMapAdapter<>(queryParameters));
        }

        return builder.build()
                .toUri();
    }

    public String getOAuthRedirect(Long requestStateId) {
        Map<String, List<String>> queryParameters = new HashMap<>();
        queryParameters.put(OAUTH_PARAM_CLIENT_ID, List.of(config.getAppId()));
        queryParameters.put(OAUTH_PARAM_SCOPE, config.getAppScopes());
        queryParameters.put(OAUTH_PARAM_SESSION, List.of("false"));
        queryParameters.put(OAUTH_PARAM_STATE, List.of(requestStateId.toString()));
        return buildUri(OAUTH_PATH_AUTHORIZE_APP, queryParameters).toString();
    }

    public boolean isAuthorized() {
        return this.tokenData != null && StringUtils.hasText(this.tokenData.getAccessToken());
    }

    public Mono<Boolean> getAuthorizationData(String code) {
        SquareOAuthRequest body = SquareOAuthRequest.builder()
                .clientId(config.getAppId())
                .clientSecret(config.getAppSecret())
                .grantType(OAUTH_GRANT_AUTHORIZATION_CODE)
                .code(code)
                .build();

        return oauthObtainToken(body)
                .map(tokenData -> {
                    this.tokenData = tokenData;
                    return true;
                });
    }

    //Hardcoded to refresh the token every 7 days starting on a Monday at 12am server time
    @Scheduled(cron = "0 0 0 * * MON")
    private void refreshToken() {
        if (this.tokenData != null && StringUtils.hasText(this.tokenData.getRefreshToken())) {
            SquareOAuthRequest body = SquareOAuthRequest.builder()
                    .clientId(this.config.getAppId())
                    .clientSecret(this.config.getAppSecret())
                    .refreshToken(this.tokenData.getRefreshToken())
                    .grantType(OAUTH_GRANT_REFRESH_TOKEN)
                    .build();

            oauthObtainToken(body)
                    .onErrorResume(error -> {
                        log.error("There was an error refreshing the token. Turn on debugging to get stack trace.");
                        log.debug("Stack trace for refreshing token error", error);
                        return Mono.empty();
                    })
                    .block();
        } else {
            this.tokenData = null;
        }
    }

    public Mono<SquareTokenData> oauthObtainToken(SquareOAuthRequest body) {
        return client
                .post()
                .uri(buildUri(OAUTH_PATH_OBTAIN_TOKEN))
                .bodyValue(body)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(SquareTokenData.class);
                    } else {
                        return Mono.error(new SquareTokenRequestException("Failed to obtain token from Square, status: " + response.statusCode().value()));
                    }
                });
    }

    public Mono<Boolean> revokeToken() {
        SquareOAuthRequest body = SquareOAuthRequest.builder()
                .clientId(config.getAppId())
                .accessToken(tokenData.getAccessToken())
                .build();

        return client
                .post()
                .uri(buildUri(OAUTH_PATH_REVOKE_TOKEN))
                .bodyValue(body)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Client " + config.getAppSecret())
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return Mono.just(true);
                    } else {
                        return Mono.error(new SquareTokenRequestException("Failed to revoke access token, status: " + response.statusCode().value()));
                    }
                });
    }
}
