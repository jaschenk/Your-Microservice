package your.microservice.core.security.idp.tasks;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import your.microservice.core.security.idp.model.base.YourEntityTokenHistory;
import your.microservice.core.util.TimeDuration;
import your.microservice.core.security.idp.repository.IdentityProviderEntityManager;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * YourMicroserviceTokenExpireTask
 * <p>
 * Provides the Interface for the Your Microservice Token Expiration Task,
 * which will delete and purge any expired tokens.
 *
 * @author jeff.a.schenk@gmail.com on 7/26/16.
 */
@Component
public class YourMicroserviceTokenExpireTaskImpl implements YourMicroserviceTokenExpireTask {

    /**
     * Common Logger
     */
    protected final static org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(YourMicroserviceTokenExpireTask.class);

    /**
     * Constants
     */
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    /**
     * Identity Provider Entity Manager.
     */
    @Autowired
    private IdentityProviderEntityManager identityProviderEntityManager;

    /**
     * initialization
     * Entered when Bean is initialized.
     */
    @PostConstruct
    public void initialization() {
        LOGGER.info("Initialization of Your Microservice Token Expire Task Implementation has been wired into runtime Environment.");
    }

    /**
     * destroyBean
     * Entered when Bean is being destroyed or torn down from the runtime Environment.
     * Simple indicate destruction...
     */
    @PreDestroy
    public void destroyBean() {
        LOGGER.info("Your Microservice Token Expire Task Implementation has been removed from the runtime Environment.");
    }

    /**
     * Perform the Purge of any Expired Tokens from the Your Microservice Token History.
     * <p>
     * Run this scheduled Migration Task Two Minutes After start-Up and
     * every 4 hours afterwards...
     */
    @Override
    @Scheduled(initialDelay = 120000, fixedRate = 14400000)
    public void purgeExpiredTokens() {
        /**
         * Simple Purge All Expired Tokens.
         */
        int purged = 0;
        TimeDuration td = new TimeDuration();
        try {
            td.start();
            /**
             * Obtain a List of All Expired Tokens
             */
            List<YourEntityTokenHistory> expiredTokens =
                    identityProviderEntityManager.readCurrentExpiredTokenHistory();
            if (expiredTokens != null && !expiredTokens.isEmpty()) {
                /**
                 * Report on those Expired Token Usage.
                 */
                LOGGER.info("Token Expiration Report: (Token to be Purged)");
                for (YourEntityTokenHistory yourEntityTokenHistory : expiredTokens) {
                     generateTokenUsage(yourEntityTokenHistory);
                }
                /**
                 * Purge all Expired Tokens.
                 */
                for (YourEntityTokenHistory yourEntityTokenHistory : expiredTokens) {
                    Integer deleted = identityProviderEntityManager.deleteTokenHistory(yourEntityTokenHistory.getJti());
                    if (deleted == null || deleted <= 0) {
                        LOGGER.warn("Unable to Delete Your Microservice Token History:'{}', Ignoring.", yourEntityTokenHistory.getJti());
                    } else {
                        purged++;
                    }
                }
            }
        } finally {
            /**
             * Show Final Statistics
             */
            td.stop();
            LOGGER.info("Token History Purge Expired Tokens Completed in '{}', Purged Tokens: '{}'.",
                    td.getElapsedtoString(), purged);
        }
    }

    /**
     * generateTokenUsage
     * @param yourEntityTokenHistory Token History Element to Log.
     */
    protected void generateTokenUsage(YourEntityTokenHistory yourEntityTokenHistory) {
         LOGGER.info("  sub:'{}', usage:'{}', Issued:'{}', Last Used:'{}', Expired:'{}'.",
                 yourEntityTokenHistory.getSubject(),
                 yourEntityTokenHistory.getUsageCount(),
                 dateFormat.format(yourEntityTokenHistory.getIssuedAt()),
                 dateFormat.format(yourEntityTokenHistory.getLastUsed()),
                 dateFormat.format(yourEntityTokenHistory.getExpiration()));
    }

}
