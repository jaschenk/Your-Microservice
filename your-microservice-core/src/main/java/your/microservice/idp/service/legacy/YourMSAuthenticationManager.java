package your.microservice.idp.service.legacy;

import your.microservice.core.system.messaging.jms.MessagePublisherService;
import your.microservice.core.util.TimeDuration;
import your.microservice.idp.model.security.YourMicroserviceUserDetails;
import your.microservice.idp.security.SecurityRepository;
import your.microservice.idp.security.YourMicroserviceSecurityConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * YourMSAuthenticationManager
 */
public class YourMSAuthenticationManager implements AuthenticationManager {

    public static String rateLimiterBlock = "RateLimiterBlock";

    private static final String RATE_EXCEEDED_MESSAGE = "Rate exceeded.";
    private static final String ACCOUNT_IS_DISABLED_MESSAGE = "Account is disabled";
    private static final String ACCOUNT_IS_EXPIRED_MESSAGE = "Account is expired";
    private static final String ACCOUNT_IS_LOCKED_MESSAGE = "Account is locked";
    private static final String USERNAME_OR_PASSWORD_IS_INVALID_MESSAGE = "Username or password is invalid";

    private static final Logger logger = LoggerFactory.getLogger(YourMicroserviceUserDetailsService.class);

    private final YourMicroserviceUserDetailsService detailsService;
    private final SecurityRepository securityRepo;
    /**
     * Message Publication Service.
     */
    protected final MessagePublisherService messagePublisherService;

    /**
     * Default Constructor for Authentication Manager.
     *
     * @param detailsService          YourMSUserDetailsService
     * @param securityRepo            Reference
     * @param messagePublisherService Reference
     */
    public YourMSAuthenticationManager(YourMicroserviceUserDetailsService detailsService, SecurityRepository securityRepo,
                                       MessagePublisherService messagePublisherService) {
        this.detailsService = detailsService;
        this.securityRepo = securityRepo;
        this.messagePublisherService = messagePublisherService;
    }

    @Override
    public Authentication authenticate(Authentication a) throws AuthenticationException {
        /**
         * Performing Timings of Authentication Interactions...
         */
        TimeDuration overall_duration = new TimeDuration();
        TimeDuration db_duration = new TimeDuration();
        TimeDuration pw_duration = new TimeDuration();

        overall_duration.start();
        if (a.getDetails() != null) {
            try {
                WebAuthenticationDetails details = (WebAuthenticationDetails) a.getDetails();
                String remoteIp = details.getRemoteAddress();
               // if (securityRepo.ipIsBlocked(remoteIp, rateLimiterBlock)) {
               //     throw new RateExceededException(RATE_EXCEEDED_MESSAGE) {
               //     };
               // }
            } catch (Exception ex) {
                // TODO If we can't determine if an IP is blocked,
                //      should we allow the attempt anyway?
                throw new BadCredentialsException(ex.getMessage());
            }
        }
        String userName = null;
        String rawPass;
        try {
            db_duration.start();
            userName = a.getPrincipal().toString();
            YourMicroserviceUserDetails userDetails = (YourMicroserviceUserDetails) detailsService.loadUserByUsername(userName);
            db_duration.stop();

            pw_duration.start();
            rawPass = a.getCredentials().toString();
            String storedHashedPass = userDetails.getPassword();
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(YourMicroserviceSecurityConstants.BCRYPT_STRENGTH_SETTING);

            if (!encoder.matches(rawPass, storedHashedPass)) {
                pw_duration.stop();
                logger.info("[FRONT END] " + USERNAME_OR_PASSWORD_IS_INVALID_MESSAGE);
                throw new BadCredentialsException(USERNAME_OR_PASSWORD_IS_INVALID_MESSAGE);
            }
            pw_duration.stop();

            if (!userDetails.isEnabled()) {
                if (!userDetails.isAccountNonLocked())
                    throw new LockedException(ACCOUNT_IS_LOCKED_MESSAGE) {
                    };

                if (!userDetails.isAccountNonExpired())
                    throw new AccountExpiredException(ACCOUNT_IS_EXPIRED_MESSAGE) {
                    };

                throw new DisabledException(ACCOUNT_IS_DISABLED_MESSAGE) {
                };
            }

            /**
             * Clear all Memory Constructs
             */
            rawPass = null;
            storedHashedPass = null;
            userDetails.shredCredentials();

            /**
             * Return Authentication Token...
             * This all will be deprecated once we integrate fully our IDaaS.
             */
            return new YourMSAuthenticationToken(userDetails);

        } catch (UsernameNotFoundException ex) {
            logger.warn("Username:[" + userName + "] Not Found, Ignoring!");
            throw new BadCredentialsException(USERNAME_OR_PASSWORD_IS_INVALID_MESSAGE);
        } finally {
            /**
             * Report our Authentication Timings
             */
            overall_duration.stop();
            logger.info("Authentication Timings: DB:" + db_duration.getElapsedtoString() +
                    ", PW: " + pw_duration + ", Overall: " +
                    overall_duration.getElapsedtoString());
        }
    }
}
