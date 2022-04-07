package org.tyto.square.client;

public class SquareApiHelper {

    // Square OAuth API parameters
    protected static final String OAUTH_PARAM_SCOPE = "scope";
    protected static final String OAUTH_PARAM_SESSION = "session";
    protected static final String OAUTH_PARAM_CLIENT_ID = "client_id";
    protected static final String OAUTH_PARAM_STATE = "state";

    // Square OAuth API Grant Types
    protected static final String OAUTH_GRANT_AUTHORIZATION_CODE = "authorization_code";
    protected static final String OAUTH_GRANT_REFRESH_TOKEN = "refresh_token";

    // Square API Paths
    protected static final String OAUTH_PATH_AUTHORIZE_APP = "/oauth2/authorize";
    protected static final String OAUTH_PATH_OBTAIN_TOKEN = "/oauth2/token";
    protected static final String OAUTH_PATH_REVOKE_TOKEN = "/oauth2/revoke";
}
