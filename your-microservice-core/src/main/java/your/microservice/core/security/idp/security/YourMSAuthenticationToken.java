package your.microservice.core.security.idp.security;

import your.microservice.core.security.idp.model.security.YourMicroserviceUserDetails;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * YourMSAuthenticationToken
 */
class YourMSAuthenticationToken extends AbstractAuthenticationToken {

    private final String credentials;
    private final String principal;
    private final Long principalID;

    public YourMSAuthenticationToken(YourMicroserviceUserDetails userDetails) {
        super(userDetails.getAuthorities());
        super.setDetails(userDetails);
        super.setAuthenticated(true);
        this.principalID = userDetails.getPrincipalID();
        this.credentials = userDetails.getPassword();
        this.principal = userDetails.getUsername();
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public String getPrincipal() {
        return principal;
    }

    public Long getPrincipalID() {
        return principalID;
    }
}
