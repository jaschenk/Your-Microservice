package your.microservice.core.security.idp.jwt;

import com.nimbusds.jwt.JWTClaimsSet;
import org.slf4j.LoggerFactory;
import org.springframework.mobile.device.Device;


import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * YourMicroserviceToken Component
 *
 * Your Microservice Standards JWT Generation:
 *
 * “iss” : Issuer
 * Static Constant: “Your Microservice”
 *
 * “sub” : Subject
 *  Depending upon type of Token will contain:
 *  Primary Email Address which initial Authentication occurred to obtain Access Token.
 *
 * “aud” : Audience
 * The IdP determine the source device the request has been originated and sets the applicable Audience.
 *   Audience Unknown
 *   Audience Mobile
 *   Audience Tablet
 *   Audience WEB
 *   Audience Bot
 *
 * “exp” : Expiration Date and Time of the Token, when Token will no longer be valid.
 *
 * “nbf” : Not Used Before Date and Time.  Token can not be used before this Date and Time.
 *
 * “iat” : Issued at Date and Time.
 *
 * “jti” : Token Identifier, Generated Random UUID.  Used for Identity Store Persistence of Token Store.
 *
 * “your.microservice” : Your Microservice Manifest
 *
 * Standard Claim Names Defined here: http://www.iana.org/assignments/jwt/jwt.xhtml
 *
 *
 * @author jeff.a.schenk@gmail.com
 */
public interface YourMicroserviceToken {

    /**
     * Common Logger
     */
    org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(YourMicroserviceToken.class);
    String LOGGING_HEADER = "IdP=> ";

    /**
     * Constants
     */
    String YOUR_ORGANIZATION_ISSUER
            = "Your Microservice";

    /**
     * Audience Types
     * TODO :: Perhaps refactor into an Enum.
     */
    String AUDIENCE_UNKNOWN = "unknown";
    String AUDIENCE_WEB = "web";
    String AUDIENCE_MOBILE = "mobile";
    String AUDIENCE_TABLET = "tablet";
    String AUDIENCE_BOT = "bot";

    /**
     * Publicly Registered Claim Names
     */
    String CLAIM_NAME_ISSUER = "iss";
    String CLAIM_NAME_SUBJECT = "sub";
    String CLAIM_NAME_AUDIENCE = "aud";
    String CLAIM_NAME_EXPIRATION = "exp";
    String CLAIM_NAME_NOT_USED_BEFORE = "nbf";
    String CLAIM_NAME_ISSUED_AT = "iat";
    String CLAIM_NAME_TOKEN_IDENTIFIER = "jti";
    /**
     * Private Claim Names
     */
    String CLAIM_NAME_YOUR_MICROSERVICE = "yms";

    /**
     * getUsernameFromToken
     *
     * @param token JWT
     * @return String containing Authenticated UserName, in our case it is the
     * Primary Email Address.  This is always the claims 'sub' or 'subject'.
     */
    String getUsernameFromToken(String token);

    /**
     * getIdentifierFromToken
     *
     * @param token JWT
     * @return String containing 'jti' or the JWT Token Identifier.
     */
    String getIdentifierFromToken(String token);

    /**
     * getIssuedDateFromToken
     *
     * @param token JWT
     * @return Date which represents 'iat' Issued At.
     */
    Date getIssuedDateFromToken(String token);

    /**
     * getExpirationDateFromToken
     *
     * @param token JWT
     * @return Date which represents 'exp' Expiration.
     */
    Date getExpirationDateFromToken(String token);

    /**
     * getAudienceFromToken
     *
     * @param token JWT
     * @return String which represents the Audience claim or 'aud'.
     */
    List<String> getAudienceFromToken(String token);

    /**
     * generateToken
     *
     * @param subject Subject Representing a Principal, example an Account's Email Address.
     * @param device Current Device Type User is performing Request from.
     * @return String Representing the constructed JWT for the specified Claims.
     */
    String generateToken(String subject, String device);

    /**
     * generateToken
     *
     * @param claims Claims to be used to build Token.
     * @return String Representing the constructed JWT for the specified Claims.
     */
    String generateToken(Map<String, Object> claims);

    /**
     * refreshToken
     * Construct a new JWT for the applicable User per the Parsed incoming JWT.
     *
     * @param token JWT
     * @return String representing new JWT for applicable Authenticated Subject.
     */
    String refreshToken(String token);

    /**
     * verifyToken
     * Performs the JWT validation for an incoming Token against User Details Obtained.
     *
     * @param token JWT to be Validated
     * @return JWTClaimsSet Obtains the Claims Set or Null.
     * @throws YourMicroserviceInvalidTokenException If not a Valid Token.
     */
    JWTClaimsSet verifyToken(String token) throws YourMicroserviceInvalidTokenException;

    /**
     * transformAudienceType, Helper Method to generate the Audience Mnemonic based upon
     * the Device Type.
     * @param device End Consumer Device Type.
     * @return String representing deduced Audience.
     */
    String transformAudienceType(Device device);

    /**
     * parseAndDumpJWT
     * Parse JWT and  Display Token Information in Logs.
     *
     * @param jwt          Token to be Parsed
     * @throws Exception if Token is not Valid.
     */
    void parseAndDumpJWT(final String jwt) throws Exception;

}
