package your.microservice.core.system.messaging.jms.listeners;

import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import your.microservice.idp.model.base.YourEntityEventHistory;
import your.microservice.idp.repository.IdentityProviderEntityManager;

/**
 * PersonEventHistoryMessageListener
 *
 * @author jeff.a.schenk@gmail.com
 */
@Component
public class EntityEventHistoryMessageListener {

    /**
     * Common Logger
     */
    protected final static org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(EntityEventHistoryMessageListener.class);

    /**
     * Identity Provider Entity Manager
     */
    @Autowired
    private IdentityProviderEntityManager identityProviderEntityManager;

    /**
     * JMS Internal Listener for Your Microservice Entity Event History Messages.
     * @param yourEntityEventHistory Payload of the Message to be Handled.
     */
    @JmsListener(destination = "your.microservice.entity.event.history")
    public void receiveEntityEventHistoryMessage(YourEntityEventHistory yourEntityEventHistory) {
        /**
         * Perform some basic Validation.
         */
        if (yourEntityEventHistory == null) {
            LOGGER.warn("Received YourEntityEventHistory Message, however payload was null, Ignoring.");
            return;
        }
        if (yourEntityEventHistory.getEventTagProperties() == null || yourEntityEventHistory.getEventTagProperties().isEmpty()) {
            LOGGER.warn("Receive YourEntityEventHistory Message, however Tag Properties were null or empty, Ignoring.");
            return;
        }
        /**
         * Show Debug Message
         */
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received Message:[" + yourEntityEventHistory.toString() + "]");
        }

        /**
         * Persist the Event ...
         */
        identityProviderEntityManager.createEventHistory(yourEntityEventHistory);
    }
}
