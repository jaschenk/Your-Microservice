package your.microservice.core.system;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

/**
 * ShutdownManager
 *
 * @author jeff.a.schenk@gmail.com on 5/18/16.
 */
@Service
public class ShutdownManagerImpl implements ShutdownManager {
    /**
     * Commons Logger
     */
    protected static final org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(ShutdownManager.class);
    /**
     * Application Context
     */
    @Autowired
    private ConfigurableApplicationContext applicationContext;

    /**
     * initiateShutdown
     *
     * @param reason Message to be Logged as reason for Shutdown.
     * @param returnCode Return code to determine Level of Shutdown.
     */
    @Override
    public void initiateShutdown(String reason, int returnCode){
        /**
         * Issue Log Message and Close Up and Exit.
         */
        LOGGER.warn("Application has internally requested to Shutdown the Container " +
                "with Reason: '{}', Return Code: '{}'", reason, returnCode);
        /**
         * Determine the Level of Shutdown, if our Return Code if 9 or above,
         * remove the JVM, immediately...
         */
        if (returnCode >= 9) {
            LOGGER.warn("=============================");
            LOGGER.warn("==    Shutting Down JVM    ==");
            LOGGER.warn("=============================");
            System.exit(returnCode);
        }
        LOGGER.warn("Closing Application Context.");
        applicationContext.close();
    }


}
