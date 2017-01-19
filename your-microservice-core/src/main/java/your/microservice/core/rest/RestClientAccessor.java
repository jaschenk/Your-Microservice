package your.microservice.core.rest;

/**
 * RestClientAccessor
 *
 * Provides Interface for RESTful Client Accessor.
 *
 * @author jeff.a.schenk@gmail.com on 2/12/16.
 */
public interface RestClientAccessor {
    /**
     * IDaaS Token Authentication Resource Path
     */
    String OAUTH_TOKEN_REQUEST_RESOURCE_PATH = "/oauth/token";

    /**
     * IDaaS Logout Resource Path
     */
    String LOGOUT_REQUEST_RESOURCE_PATH = "/logout";

    /**
     * Required Headers
     */
    String AUTHORIZATION_HEADER_NAME = "Authorization";
    String AUTHORIZATION_HEADER_BEARER_VALUE = "Bearer ";
    String ORIGIN_HEADER_NAME = "Origin";
    String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    String CONTENT_TYPE_FORM_HEADER_VALUE = "application/x-www-form-urlencoded";
    String ACCEPT_HEADER_NAME = "Accept";
    String ACCEPT_HEADER_FORM_HEADER_DEFAULT_VALUE = "application/json;charset=UTF-8";
    String CONTENT_TYPE_JSON = "application/json";
    String UTF8 = "UTF-8";

    /**
     * Parameter Name Constants
     */
    String GRANT_TYPE_PNAME = "grant_type";
    String USERNAME_PNAME = "username";
    String PASSWORD_PNAME = "password";
    String REFRESH_TOKEN_PNAME = "refresh_token";

    /**
     * getAccessToken
     *
     * Provides initial Method to gain Access to a Protected Resource, which is
     * to obtain an Access Token for the Protected Resource.
     *
     * @param url Your Microservice Enterprise Eco-System
     * @param principal User Email Address or UserId to be validated against a Stormpath Account Store.
     * @param credentials User Credentials or Password to be validated against a Stormpath Account Store.
     * @return RestClientAccessObject which contains the Access Token, Refresh Token, Token Type and
     * Token Expiration in Milliseconds.
     */
    RestClientAccessObject getAccessToken(String url, String principal, String credentials);

    /**
     * getAccessToken
     *
     * Provides subsequent Method to gain Access to a Protected Resource, which is
     * to use a Refresh Token to obtain a new Access Token for the Protected Resource.
     *
     * @param url Your Microservice Enterprise Eco-System
     * @param restClientAccessObject Access Object, which contains Refresh Token.
     * @return RestClientAccessObject which contains the new Access Token, Refresh Token, Token Type and
     * Token Expiration in Milliseconds.
     */
    RestClientAccessObject getAccessToken(String url, RestClientAccessObject restClientAccessObject);

    /**
     * get
     *
     * Perform RESTful 'GET' Method using specified URL.
     *
     * @param url Your Microservice Enterprise Eco-System
     * @param restClientAccessObject Access Object, which contains Access Token.
     * @return Object Response
     */
    Object get(String url, RestClientAccessObject restClientAccessObject);

    /**
     * post
     *
     * Perform RESTful 'POST' Method using specified URL.
     *
     * @param url Your Microservice Enterprise Eco-System
     * @param restClientAccessObject Access Object, which contains Access Token.
     * @return Object Response
     */
    Object post(String url, RestClientAccessObject restClientAccessObject);

    /**
     * post
     *
     * Perform RESTful 'POST' Method using specified URL.
     *
     * @param url Your Microservice Enterprise Eco-System
     * @param payload DTO Payload to be sent with Resource.
     * @param restClientAccessObject Access Object, which contains Access Token.
     * @return Object Response
     */
    Object post(String url, Object payload, RestClientAccessObject restClientAccessObject);

    /**
     * put
     *
     * Perform RESTful 'PUT' Method using specified URL.
     *
     * @param url Your Microservice Enterprise Eco-System
     * @param restClientAccessObject Access Object, which contains Access Token.
     * @return Object Response
     */
    Object put(String url, RestClientAccessObject restClientAccessObject);

    /**
     * put
     *
     * Perform RESTful 'PUT' Method using specified URL.
     *
     * @param url Your Microservice Enterprise Eco-System
     * @param payload DTO Payload to be sent with Resource.
     * @param restClientAccessObject Access Object, which contains Access Token.
     * @return Object Response
     */
    Object put(String url, Object payload, RestClientAccessObject restClientAccessObject);

    /**
     * delete
     *
     * Perform RESTful 'DELETE' Method using specified URL.
     *
     * @param url Your Microservice Enterprise Eco-System
     * @param restClientAccessObject Access Object, which contains Access Token.
     * @return Object Response
     */
    Object delete(String url, RestClientAccessObject restClientAccessObject);

    /**
     * logout
     *
     * Perform Logout to expire supplied Refresh Token.
     *
     * @param url Your Microservice Enterprise Eco-System
     * @param restClientAccessObject  Access Object, which contains Access Token.
     * @return int Logout HTTPs Return Code.
     */
    int logout(String url, RestClientAccessObject restClientAccessObject);

}
