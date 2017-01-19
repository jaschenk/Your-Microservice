package your.microservice.idp.security;

import javax.servlet.http.HttpServletRequest;

/**
 * YourMicroserviceSecurityConstants
 *
 * @author jeff.a.schenk@gmail.com on 8/3/15.
 */
public final class YourMicroserviceSecurityConstants {

    /**
     * Should provide a Maximum of 690ms+/- to perform Matches and Encoding Operations.
     */
    public static final int BCRYPT_STRENGTH_SETTING = 13;

    /**
     * Your Microservice Incoming Authorization Token Header Specification
     */
    public static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    public static final String AUTHORIZATION_HEADER_BEARER_VALUE = "Bearer ";

    /**
     * obtainAuthorizationBearerToken from its Original HTTP Request Object.
     *
     * @param httpRequest Incoming Request
     * @return String containing the Authorization Token aka access Token aka our JWT.
     * This can be returned as null, if we do not parse the JWT from the Bearer Header.
     */
    protected static String obtainAuthorizationBearerToken(HttpServletRequest httpRequest) {
        return obtainAuthorizationBearerToken(httpRequest.getHeader(YourMicroserviceSecurityConstants.AUTHORIZATION_HEADER_NAME));
    }

    /**
     * obtainAuthorizationBearerToken from its String Form.
     *
     * @param authorizationToken JWT which may have a Bearer Prefix.
     * @return String containing the Authorization Token aka access Token aka our JWT.
     */
    protected static String obtainAuthorizationBearerToken(String authorizationToken) {
        if (authorizationToken != null && !authorizationToken.isEmpty() &&
                authorizationToken.startsWith(YourMicroserviceSecurityConstants.AUTHORIZATION_HEADER_BEARER_VALUE)) {
            /**
             * Return the JWT, less the Bearer static String prefix for the Header Contents.
             */
           return authorizationToken.substring(YourMicroserviceSecurityConstants.AUTHORIZATION_HEADER_BEARER_VALUE.length()).trim();
        }
        /**
         * Check for a placeholder Token from the Swagger Console.
         * Simply Nullify.
         */
        else if (authorizationToken != null && !authorizationToken.isEmpty() &&
                authorizationToken.equalsIgnoreCase("apiKey")) {
            return null;
        }
        /**
         * Return Original Token.
         */
        return authorizationToken;
    }

}
