package your.microservice.core.system.messaging.jms.listeners;

import your.microservice.core.dm.dto.history.EntityEventHistoryDTO;
import org.slf4j.LoggerFactory;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * PersonEventHistoryMessageListener
 *
 * @author jeff.a.schenk@gmail.com on 11/11/15.
 */
@Component
public class PersonEventHistoryMessageListener {

    /**
     * Common Logger
     */
    protected final static org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(PersonEventHistoryMessageListener.class);

    /**
     * JMS Internal Listener for Your Microservice Entity Event History Messages.
     * @param personEventHistory Payload of the Message to be Handled.
     */
    @JmsListener(destination = "your.microservice.entity.event.history")
    public void receivePersonEventHistoryMessage(EntityEventHistoryDTO personEventHistory) {
        /**
         * Perform some basic Validation.
         */
        if (personEventHistory == null) {
            LOGGER.warn("Received YourEntityEventHistory Message, however payload was null, Ignoring.");
            return;
        }
        if (personEventHistory.getEventTagProperties() == null || personEventHistory.getEventTagProperties().isEmpty()) {
            LOGGER.warn("Receive YourEntityEventHistory Message, however Tag Properties were null or empty, Ignoring.");
            return;
        }
        /**
         * Show Debug Message
         */
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received Message:[" + personEventHistory.toString() + "]");
        }

    }
}
