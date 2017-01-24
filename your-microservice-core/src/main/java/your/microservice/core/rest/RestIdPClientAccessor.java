package your.microservice.core.rest;

/**
 * RestIdPClientAccessor
 *
 * Provides Interface for RESTful Client Accessor.
 *
 * @author jeff.a.schenk@gmail.com
 */
public interface RestIdPClientAccessor {
    /**
     * Your Microservice IdP Default Token Authentication Resource Path
     */
    String YOUR_MICROSERVICE_IDP_TOKEN_REQUEST_RESOURCE_PATH = "/api/auth";
    String YOUR_MICROSERVICE_IDP_TOKEN_LOGOUT_RESOURCE_PATH = "/api/auth/logout";

    /**
     * Required Headers
     */
    String AUTHORIZATION_HEADER_NAME = "Authorization";
    String AUTHORIZATION_HEADER_BEARER_VALUE = "Bearer ";
    String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    String ACCEPT_HEADER_NAME = "Accept";
    String ACCEPT_HEADER_JSON_HEADER_DEFAULT_VALUE = "application/json;charset=UTF-8";
    String CONTENT_TYPE_JSON = "application/json";
    String UTF8 = "UTF-8";
    
    String ORIGIN_HEADER_NAME = "Origin";

    /**
     * Parameter Name Constants
     */
    String USERNAME_PNAME = "username";
    String PASSWORD_PNAME = "password";

    /**
     * getAccessToken
     *
     * Provides initial Method to gain Access to a Protected Resource, which is
     * to obtain an Access Token for the Protected Resource.
     *
     * @param url Enterprise Eco-System
     * @param principal User Email Address or UserId to be validated against a IdP Account Store.
     * @param credentials User Credentials or Password to be validated against a IdP Account Store.
     * @return RestIdPClientAccessObject which contains the Access Token, Token Type and
     * Token Expiration in Milliseconds.
     */
    RestIdPClientAccessObject getAccessToken(String url, String principal, String credentials);

    /**
     * getAccessToken
     *
     * Provides subsequent Method to gain Access to a Protected Resource, which is
     * to use a Refresh Token to obtain a new Access Token for the Protected Resource.
     *
     * @param url Enterprise Eco-System
     * @param RestIdPClientAccessObject Access Object, which contains Refresh Token.
     * @return RestIdPClientAccessObject which contains the new Access Token, Refresh Token, Token Type and
     * Token Expiration in Milliseconds.
     */
    RestIdPClientAccessObject getAccessToken(String url, RestIdPClientAccessObject RestIdPClientAccessObject);

    /**
     * get
     *
     * Perform RESTful 'GET' Method using specified URL.
     *
     * @param url Enterprise Eco-System
     * @param RestIdPClientAccessObject Access Object, which contains Access Token.
     * @return Object Response
     */
    Object get(String url, RestIdPClientAccessObject RestIdPClientAccessObject);

    /**
     * post
     *
     * Perform RESTful 'POST' Method using specified URL.
     *
     * @param url Enterprise Eco-System
     * @param RestIdPClientAccessObject Access Object, which contains Access Token.
     * @return Object Response
     */
    Object post(String url, RestIdPClientAccessObject RestIdPClientAccessObject);

    /**
     * post
     *
     * Perform RESTful 'POST' Method using specified URL.
     *
     * @param url Enterprise Eco-System
     * @param payload DTO Payload to be sent with Resource.
     * @param RestIdPClientAccessObject Access Object, which contains Access Token.
     * @return Object Response
     */
    Object post(String url, Object payload, RestIdPClientAccessObject RestIdPClientAccessObject);

    /**
     * put
     *
     * Perform RESTful 'PUT' Method using specified URL.
     *
     * @param url Enterprise Eco-System
     * @param RestIdPClientAccessObject Access Object, which contains Access Token.
     * @return Object Response
     */
    Object put(String url, RestIdPClientAccessObject RestIdPClientAccessObject);

    /**
     * put
     *
     * Perform RESTful 'PUT' Method using specified URL.
     *
     * @param url Enterprise Eco-System
     * @param payload DTO Payload to be sent with Resource.
     * @param RestIdPClientAccessObject Access Object, which contains Access Token.
     * @return Object Response
     */
    Object put(String url, Object payload, RestIdPClientAccessObject RestIdPClientAccessObject);

    /**
     * delete
     *
     * Perform RESTful 'DELETE' Method using specified URL.
     *
     * @param url Enterprise Eco-System
     * @param RestIdPClientAccessObject Access Object, which contains Access Token.
     * @return Object Response
     */
    Object delete(String url, RestIdPClientAccessObject RestIdPClientAccessObject);

    /**
     * logout
     *
     * Perform Logout to expire supplied Refresh Token.
     *
     * @param url Enterprise Eco-System
     * @param RestIdPClientAccessObject  Access Object, which contains Access Token.
     * @return int Logout HTTPs Return Code.
     */
    int logout(String url, RestIdPClientAccessObject RestIdPClientAccessObject);

}
