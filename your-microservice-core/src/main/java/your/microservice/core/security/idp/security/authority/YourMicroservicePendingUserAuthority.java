package your.microservice.core.security.idp.security.authority;

import org.springframework.security.core.GrantedAuthority;

/**
 *
 */
public class YourMicroservicePendingUserAuthority implements GrantedAuthority {

    @Override
    public String getAuthority() {
        return "ROLE_PENDING_USER";
    }
}
