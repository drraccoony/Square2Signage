package org.tyto.square.client.model.square;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.tyto.square.client.SquareApiHelper;

/**
 * Use this class when
 */

@Data
@Setter(AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SquareOAuthRequest {
    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("client_secret")
    private String clientSecret;
    private String code;
    @JsonProperty("grant_type")
    private String grantType;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("access_token")
    private String accessToken;

    public static SquareOAuthRequest buildAuthorizeTokenBody(String clientId, String clientSecret, String code) {
        return SquareOAuthRequest.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .code(code)
                .grantType(SquareApiHelper.OAUTH_GRANT_AUTHORIZATION_CODE)
                .build();
    }

    public static SquareOAuthRequest buildRefreshTokenBody(String clientId, String refreshToken) {
        return SquareOAuthRequest.builder()
                .clientId(clientId)
                .refreshToken(refreshToken)
                .grantType(SquareApiHelper.OAUTH_GRANT_REFRESH_TOKEN)
                .build();
    }

    public static SquareOAuthRequest buildRevokeTokenBody(String clientId, String accessToken) {
        return SquareOAuthRequest.builder()
                .clientId(clientId)
                .accessToken(accessToken)
                .build();
    }
}
