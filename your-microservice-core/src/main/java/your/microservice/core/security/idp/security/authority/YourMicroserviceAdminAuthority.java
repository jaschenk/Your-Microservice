package your.microservice.core.security.idp.security.authority;

import org.springframework.security.core.GrantedAuthority;

/**
 *
 */
public class YourMicroserviceAdminAuthority implements GrantedAuthority {

    @Override
    public String getAuthority() {
        return "ROLE_ADMIN";
    }
}
