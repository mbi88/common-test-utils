package endpoint;

/**
 * Contract for access control checks using auth tokens.
 */
public interface Access {

    /**
     * Ensures access is granted with a valid auth token.
     * Expected: 2xx status code.
     */
    void testCanGetAccess();

    /**
     * Ensures access is denied with an invalid auth token.
     * Expected: 401 Unauthorized.
     */
    void testCantGetAccessWithInvalidToken();

    /**
     * Ensures access is denied when Authorization header is missing.
     * Expected: 401 Unauthorized.
     */
    void testCantGetAccessWithoutAuth();
}
