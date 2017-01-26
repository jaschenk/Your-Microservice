package your.microservice.core.security.idp.service.authority;

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
