package your.microservice.idp.service.authority;

import org.springframework.security.core.GrantedAuthority;

/**
 *
 */
public class YourMicroservicePendingUserAuthority implements GrantedAuthority {

    @Override
    public String getAuthority() {
        return "ROLE_PENDING_USER"; //To change body of generated methods, choose Tools | Templates.
    }
}
