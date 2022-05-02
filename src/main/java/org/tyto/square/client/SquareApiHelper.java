package org.tyto.square.client;

import java.util.List;
import java.util.Map;

public class SquareApiHelper {

    // Square OAuth
    public static final String OAUTH_PARAM_SCOPE = "scope";
    public static final String OAUTH_PARAM_SESSION = "session";
    public static final String OAUTH_PARAM_CLIENT_ID = "client_id";
    public static final String OAUTH_PARAM_STATE = "state";
    public static final String OAUTH_GRANT_AUTHORIZATION_CODE = "authorization_code";
    public static final String OAUTH_GRANT_REFRESH_TOKEN = "refresh_token";
    public static final String OAUTH_PATH_AUTHORIZE_APP = "/oauth2/authorize";
    public static final String OAUTH_PATH_OBTAIN_TOKEN = "/oauth2/token";
    public static final String OAUTH_PATH_REVOKE_TOKEN = "/oauth2/revoke";

    // Square Catalog API
    public static final Map<String, List<String>> CATALOG_LIST_PARAMS = Map.of("types", List.of("ITEM"));
    public static final String CATALOG_LIST_PATH = "/v2/catalog/list";

    // Square Inventory API
    public static final String INVENTORY_BATCH_RETRIEVE_COUNTS_PATH = "/v2/inventory/counts/batch-retrieve";
    public static final String INVENTORY_BATCH_RETRIEVE_COUNTS_BODY_IDS = "catalog_object_ids";
}
