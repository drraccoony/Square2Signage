package org.tyto.square.service;

import lombok.extern.slf4j.Slf4j;
import org.tyto.square.client.SquareRestClient;
import org.tyto.square.configuration.SquareConfiguration;
import org.tyto.square.exception.SquareStateIdException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

@Slf4j
@Service
public class SquareService {

    private final SquareConfiguration config;
    private final SquareRestClient client;
    private Long cachedStateId;
    private Long stateExpiration;

    @Autowired
    public SquareService(SquareConfiguration config, SquareRestClient client) {
        this.config = config;
        this.client = client;
        this.stateExpiration = System.currentTimeMillis();
    }

    public String startSquareOAuthFlow() {
        try {
            this.cachedStateId = SecureRandom.getInstanceStrong().nextLong();
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to generate secure state id.");
            this.cachedStateId = new Random().nextLong();
        }
        this.stateExpiration = config.getStateIdTtl() + System.currentTimeMillis();
        return client.getOAuthRedirect(this.cachedStateId);
    }

    public Mono<Boolean> finishSquareOAuthFlow(String code, Long stateId) {
        if (stateId != null && !stateId.equals(cachedStateId)) {
            log.error("State ids do not match -> cached: " + cachedStateId + " provided: " + stateId);
            return Mono.error(new SquareStateIdException("Provided state ID does not match current authorization session, please re-initiate Square authorization."));
        } else if (System.currentTimeMillis() > stateExpiration) {
            log.error("Provided state ID has expired.");
            return Mono.error(new SquareStateIdException("Provided state ID has expired, please re-initiate Square authorization."));
        }

        return client.getAuthorizationData(code);
    }

    public boolean checkIfAuthorized() {
        return client.isAuthorized();
    }

    public Mono<Boolean> revokeAccessToken() {
        if (!client.isAuthorized()) {
            return Mono.just(true);
        } else {
            return client.revokeToken()
                    .onErrorResume(error -> {
                        log.error("Failed to revoke access token due to error: " + error.getMessage());
                        log.debug("Error when revoking access", error);
                        return Mono.just(false);
                    });
        }
    }
}
