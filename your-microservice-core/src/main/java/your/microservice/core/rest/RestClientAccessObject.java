package your.microservice.core.rest;

import java.util.Map;

/**
 * RestClientAccessObject
 *
 * @author jeff.a.schenk@gmail.com on 2/12/16.
 */
public class RestClientAccessObject {
    /**
     * Constants
     */
    private static final String ACCESS_TOKEN_PROPERTY_NAME = "access_token";
    private static final String REFRESH_TOKEN_PROPERTY_NAME = "refresh_token";
    private static final String TOKEN_TYPE_PROPERTY_NAME = "token_type";
    private static final String TOKEN_EXPIRES_PROPERTY_NAME = "expires_in";

    /**
     * Access Properties.
     */
    private Map<String, Object> accessProperties;

    /**
     * Default Constructor
     * @param accessProperties Access Properties
     */
    public RestClientAccessObject(Map<String, Object> accessProperties) {
        this.accessProperties = accessProperties;
    }

    public String getAccessToken() {
       return (String) this.accessProperties.get(ACCESS_TOKEN_PROPERTY_NAME);
    }

    public String getRefreshToken() {
        return (String) this.accessProperties.get(REFRESH_TOKEN_PROPERTY_NAME);
    }

    public String getTokenType() {
        return (String) this.accessProperties.get(TOKEN_TYPE_PROPERTY_NAME);
    }

    public Integer getExpires() {
        return (Integer) this.accessProperties.get(TOKEN_EXPIRES_PROPERTY_NAME);
    }

}
