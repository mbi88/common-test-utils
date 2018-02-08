package endpoint;

/**
 * Created by mbi on 10/6/16.
 */
public interface Access {

    /**
     * Assure access is granted with valid auth token
     * "allowAll": true
     * Expected status code: 2XX
     */
    void testCanGetAccess();

    /**
     * Assure access is denied if it's not allowed
     * "allowAll": false, "allowedCompanies": [<new_company_id>]
     * Expected status code: 403
     */
    void testCantGetAccessIfNotAllowed();

    /**
     * Assure access is denied if token is invalid
     * Expected status code: 401
     */
    void testCantGetAccessWithInvalidToken();

    /**
     * Assure no access if request performed without Authorization header
     * Expected status code: 401
     */
    void testCantGetAccessWithoutAuth();

    /**
     * Assure access is granted with valid tests.auth token
     * "allowAll": false, "allowedCompanies": [<company_id>]
     * Expected status code: 2XX
     */
    void testCanGetAccessIfCompanyAllowed();
}
