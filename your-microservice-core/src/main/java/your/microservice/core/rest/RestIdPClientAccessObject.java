package your.microservice.core.rest;

import java.util.Map;

/**
 * RestIdPClientAccessObject
 *
 * @author jeff.a.schenk@gmail.com
 */
public class RestIdPClientAccessObject {
    /**
     * Constants
     */
    private static final String ACCESS_TOKEN_PROPERTY_NAME = "access_token";
    private static final String TOKEN_EXPIRES_PROPERTY_NAME = "expires_in";
    private static final String TOKEN_TYPE_PROPERTY_NAME = "token_type";

    /**
     * Access Properties.
     */
    private Map<String, Object> accessProperties;

    /**
     * Default Constructor
     * @param accessProperties Access Properties
     */
    public RestIdPClientAccessObject(Map<String, Object> accessProperties) {
        this.accessProperties = accessProperties;
    }

    public String getAccessToken() {
       return (String) this.accessProperties.get(ACCESS_TOKEN_PROPERTY_NAME);
    }

    public Integer getExpires() {
        return (Integer) this.accessProperties.get(TOKEN_EXPIRES_PROPERTY_NAME);
    }
    
    public String getTokenType() {
        return (String) this.accessProperties.get(TOKEN_TYPE_PROPERTY_NAME);
     }

}
