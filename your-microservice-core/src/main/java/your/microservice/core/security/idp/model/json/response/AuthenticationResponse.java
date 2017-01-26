package your.microservice.core.security.idp.model.json.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import your.microservice.core.dm.serialization.JsonDateSerializer;
import your.microservice.core.security.idp.security.YourMicroserviceSecurityConstants;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

public class AuthenticationResponse implements Serializable {

    private static final long serialVersionUID = -6624726180748515507L;

    /**
     * Constants
     */
    private static final Integer DEFAULT_EXPIRES_IN = 14400; // Default 4 Hours.

    /**
     * Your Microservice JWT Access Token
     */
    private String access_token;
    /**
     * token_type
     * REQUIRED.  The type of the token issued as described in
     * Value is case insensitive.
     */
    private String token_type;
    /**
     * expires_in
     * RECOMMENDED.  The lifetime in seconds of the access token.  For
     * example, the value "14400" denotes that the access token will
     * expire in four hours from the time the response was generated.
     * If omitted, the authorization server SHOULD provide the
     * expiration time via other means or document the default value.
     */
    private Integer expires_in;

    /**
     * Current Time the
     */
    @JsonSerialize(using=JsonDateSerializer.class)
    private Date issuedAt;

    /**
     * Default Response.
     */
    public AuthenticationResponse() {
        super();
        this.setExpires_in(DEFAULT_EXPIRES_IN);
        this.setToken_type(YourMicroserviceSecurityConstants.AUTHORIZATION_HEADER_BEARER_VALUE.trim());
        this.issuedAt = Date.from(Instant.now());
    }

    /**
     * Default Response with Supplied Parameters.
     * @param token JWE
     * @param expires_in Expiration
     */
    public AuthenticationResponse(String token, Integer expires_in) {
        this.setAccess_token(token);
        this.setExpires_in(expires_in);
        this.setToken_type(YourMicroserviceSecurityConstants.AUTHORIZATION_HEADER_BEARER_VALUE.trim());
        this.issuedAt = Date.from(Instant.now());
    }

    /**
     * @return Integer expires_in
     */
    public Integer getExpires_in() {
        return expires_in;
    }

    /**
     * @param expires_in the expires_in to set
     */
    public void setExpires_in(Integer expires_in) {
        this.expires_in = expires_in;
    }

    /**
     * @return the access_token
     */
    public String getAccess_token() {
        return access_token;
    }

    /**
     * @param access_token the access_token to set
     */
    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    /**
     * @return the token_type
     */
    public String getToken_type() {
        return token_type;
    }

    /**
     * @param token_type the token_type to set
     */
    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public Date getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Date issuedAt) {
        this.issuedAt = issuedAt;
    }
}
