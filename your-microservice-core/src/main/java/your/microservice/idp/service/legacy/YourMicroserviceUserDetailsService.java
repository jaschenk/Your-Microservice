package your.microservice.idp.service.legacy;

import your.microservice.idp.model.security.YourMicroserviceUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class YourMicroserviceUserDetailsService implements UserDetailsService {

    /**
     * Query Updated per SAAS-107 to hide any Logically Deleted Entities.
     */
    private static final String findPersonByEmail =
            "select * from Person where emails contains (email = ?) and objState <> 'DELETED' limit 1";
    /**
     * Data Access Factory
     */
    //@Autowired
    //private DataAccessFactory dataAccessFactory;

    @Override
    public UserDetails loadUserByUsername(String uName) throws UsernameNotFoundException {
        if (uName == null || uName.isEmpty()) {
            throw new PreAuthenticatedCredentialsNotFoundException("No User Email Address Supplied for Obtaining User, Ignoring!");
        }
        //try (ODatabaseDocumentTx db = dataAccessFactory.getReader()) {
        //    List<ODocument> result = db.command(new OSQLSynchQuery<>(findPersonByEmail)).execute(uName.toLowerCase());

        //    if (result.isEmpty()) {
        //        throw new UsernameNotFoundException("No User with email address '" + uName + "' could be found.");
        //    }

        //    ODocument oPerson = result.get(0);
            return new YourMicroserviceUserDetails(null);
        //}
    }
}
