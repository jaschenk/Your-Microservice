package your.microservice.idp.service.legacy;

import your.microservice.idp.model.security.YourMicroserviceUserDetails;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class YourMSCurrentUserService implements CurrentUserService {

    private final String anonUser = "anonymousUser";

    //@Autowired
    //private DatabaseManager dbManager;

    //@Autowired
    //private CurrentUserRepository currentUserRepository;

    //public void inject(DatabaseManager dbManager) {
    //    this.dbManager = dbManager;
    //}

    private final static String AUTHORITIES_FOR_ORG = "select * from Organization where uuid = :uuid and personnel contains ( person = :person) limit 1";
    private final static String PERMISSIONS_BY_ID = "select * from [";
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(YourMSCurrentUserService.class);

    private Authentication getAuth() {
        return SecurityContextHolder.getContext()
                .getAuthentication();
    }

    @Override
    public boolean isAuthenticated() {
        if (getAuth() == null) {
            return false;
        }
        Object principal = getAuth().getPrincipal();
        if (principal.getClass().equals(String.class) && principal.equals(anonUser)) {
            return false;
        } else {
            return getAuth().isAuthenticated();
        }
    }

    @Override
    public Object getRID() {
        if (isAuthenticated()) {
            YourMicroserviceUserDetails userDetails = getDetails();
            return userDetails.getRid();
        } else {
            // TODO throw exception if false?
            return null;
        }
    }

    @Override
    public YourMicroserviceUserDetails getDetails() {
        if (isAuthenticated()) {
            return (YourMicroserviceUserDetails) getAuth().getPrincipal();
        } else {
            // TODO throw exception if false?
            return null;
        }
    }

    @Override
    public boolean hasAuthority(GrantedAuthority authority, String orgId) {
        //public boolean hasAuthority(GrantedAuthority authority, ODocument forOrganization) {
        if (!isAuthenticated()) {
            return false;
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.addAll(getAuth().getAuthorities());
        authorities.addAll(currentUserAuthoritiesForOrg(orgId));

        for (GrantedAuthority grant : authorities) {
            //logger.info("Checking granted auth: {} // against auth: {}", grant.getAuthority(), authority.getAuthority());
            if (authority.getAuthority().equals(grant.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public GrantedAuthority orgAdminAuthorityType() {
        return new OrgAdmin();
    }

    private class OrgAdmin implements GrantedAuthority {

        OrgAdmin() {
        }

        @Override
        public String getAuthority() {
            return "IS_ORG_ADMIN";
        }
    }

    private class YourMicroserviceAuthority implements GrantedAuthority {

        private final String authName;

        YourMicroserviceAuthority(String authName) {
            this.authName = authName;
        }

        @Override
        public String getAuthority() {
            return this.authName;
        }
    }


    @Override
    public List<GrantedAuthority> currentUserAuthoritiesForOrg(String orgUuid) {
        //public List<GrantedAuthority> currentUserAuthoritiesForOrg(ODocument org) {
        List<GrantedAuthority> grantedForOrg = new ArrayList<>();
        Object personRID = getRID();


        return grantedForOrg;
    }


}
