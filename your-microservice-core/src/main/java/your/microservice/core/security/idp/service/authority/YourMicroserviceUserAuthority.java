package your.microservice.core.security.idp.service.authority;

import org.springframework.security.core.GrantedAuthority;

/**
 *
 */
public class YourMicroserviceUserAuthority implements GrantedAuthority {

    @Override
    public String getAuthority() {
        return "ROLE_USER";
    }
}
