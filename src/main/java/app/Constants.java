package app;

import static app.Configuration.*;

public class Constants {

    // Tests environments
    public static final String USERS_URL = apiUrl + "v1/users/";

    // Auth env variables
    public static final String AUTH_SECRET_KEY = secret;
    public static final boolean IS_BASE64_ENCODED = isBase64Encoded;
}
