package endpoint;

/**
 * Created by mbi on 10/6/16.
 */
public interface Access {

    /**
     * Assure access is granted with valid auth token.
     * "allowAll": true
     * Expected status code: 2XX
     */
    void testCanGetAccess();

    /**
     * Assure access is denied if token is invalid.
     * Expected status code: 401
     */
    void testCantGetAccessWithInvalidToken();

    /**
     * Assure no access if request performed without Authorization header.
     * Expected status code: 401
     */
    void testCantGetAccessWithoutAuth();
}
