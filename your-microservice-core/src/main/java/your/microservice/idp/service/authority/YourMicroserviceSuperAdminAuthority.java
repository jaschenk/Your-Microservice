package your.microservice.idp.service.authority;

import org.springframework.security.core.GrantedAuthority;

/**
 *
 */
public class YourMicroserviceSuperAdminAuthority implements GrantedAuthority {

    @Override
    public String getAuthority() {
        return "ROLE_SUPER_ADMIN";
    }
}
