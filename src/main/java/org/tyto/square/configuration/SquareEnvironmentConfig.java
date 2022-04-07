package org.tyto.square.configuration;

public enum SquareEnvironmentConfig {
    PRODUCTION("connect.squareup.com", "https"),
    SANDBOX("connect.squareupsandbox.com", "https");

    private final String host;
    private final String httpScheme;

    SquareEnvironmentConfig(String host, String httpScheme) {
        this.host = host;
        this.httpScheme = httpScheme;
    }

    public String getHost() {
        return this.host;
    }

    public String getHttpScheme() {
        return this.httpScheme;
    }
}
