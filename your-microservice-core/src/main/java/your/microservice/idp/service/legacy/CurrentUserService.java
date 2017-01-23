package your.microservice.idp.service.legacy;

import your.microservice.idp.model.security.YourMicroserviceUserDetails;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

/**
 *
 */
public interface CurrentUserService {
    boolean isAuthenticated();

    YourMicroserviceUserDetails getDetails();

    boolean hasAuthority(GrantedAuthority authority, String orgUuid);

    GrantedAuthority orgAdminAuthorityType();

    List<GrantedAuthority> currentUserAuthoritiesForOrg(String orgUuid);


}
