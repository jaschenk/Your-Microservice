package your.microservice.idp.service.legacy;

import your.microservice.idp.model.security.YourMicroserviceUserDetails;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 *
 */
class YourMSAuthenticationToken extends AbstractAuthenticationToken {

    private final String credentials;
    private final Object principal;
    private final String principalUUID;

    public YourMSAuthenticationToken(YourMicroserviceUserDetails userDetails) {
        super(userDetails.getAuthorities());
        super.setDetails(userDetails);
        super.setAuthenticated(true);
        this.principal = userDetails.getRid();
        this.credentials = userDetails.getPassword();
        this.principalUUID = userDetails.getPrincipalUUID();
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public String getPrincipalUUID() {
        return principalUUID;
    }
}
