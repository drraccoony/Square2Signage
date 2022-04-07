package org.tyto.square.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SquareOAuthResponse {
    private String refreshToken;
    private String expiresAt;
    private String accessToken;
    private String tokenType;
    private String merchantId;
}
