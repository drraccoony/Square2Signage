package org.tyto.square.client.model.square;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.tyto.square.exception.SquareTokenRequestException;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SquareTokenData {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("expires_at")
    private String expiresAt;
    @JsonProperty("merchant_id")
    private String merchantId;
    @JsonProperty("refresh_token")
    private String refreshToken;

    public void refreshTokenData(SquareTokenData refreshTokenResponse) throws SquareTokenRequestException {
        if (StringUtils.hasText(refreshTokenResponse.getAccessToken())) {
            this.setRefreshToken(refreshTokenResponse.getAccessToken());
            log.debug("Access token has been refreshed.");
        } else {
            log.error("Failed to retrieve access token from refresh token response.");
            throw new SquareTokenRequestException("No access token is present in token response.");
        }

        if (StringUtils.hasText(refreshTokenResponse.getExpiresAt())) {
            this.setExpiresAt(refreshTokenResponse.getExpiresAt());
            log.debug("Expires at has been updated");
        }

        if (StringUtils.hasText(refreshTokenResponse.getRefreshToken()) &&
                !this.refreshToken.equals(refreshTokenResponse.getRefreshToken())) {
            this.setRefreshToken(refreshTokenResponse.getRefreshToken());
            log.debug("Refresh token has been updated");
        }
    }
}
