package your.microservice.core.security.idp.security;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import your.microservice.core.security.idp.model.base.YourEntity;
import your.microservice.core.security.idp.model.security.YourMicroserviceUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.stereotype.Service;
import your.microservice.core.security.idp.repository.IdentityProviderEntityManager;

/**
 * YourMicroserviceUserDetailsService
 *
 * @author jeff.a.schenk@gmail.com
 */
@Service
public class YourMicroserviceUserDetailsService implements UserDetailsService {
    /**
     * Common Logger
     */
    protected final static org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(YourMicroserviceUserDetailsService.class);
    /**
     * Entity Manager
     */
    @Autowired
    private IdentityProviderEntityManager identityProviderEntityManager;

    @Override
    public UserDetails loadUserByUsername(String uName) throws UsernameNotFoundException {
        YourEntity yourEntity =  null;
        if (uName == null || uName.isEmpty()) {
            throw new PreAuthenticatedCredentialsNotFoundException("No User Email Address Supplied for Obtaining User, Ignoring!");
        }
            LOGGER.info("Authenticating:[{}]", uName);
            yourEntity = identityProviderEntityManager.findYourEntityByEmail(uName);
            if (yourEntity == null) {
                LOGGER.warn("YourEntity Object Not Found based Upon Email:[{}]",uName);
                throw new UsernameNotFoundException("No User with email address '" + uName + "' could be found.");
            }
        LOGGER.info("YourEntity Object Found based Upon Email:[{}]",uName);
        return new YourMicroserviceUserDetails(yourEntity);
    }
    
}
