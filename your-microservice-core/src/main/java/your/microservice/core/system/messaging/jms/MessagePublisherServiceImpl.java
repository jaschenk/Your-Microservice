package your.microservice.core.system.messaging.jms;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import your.microservice.core.security.idp.model.base.YourEntity;
import your.microservice.core.security.idp.model.base.YourEntityEventHistory;
import your.microservice.core.security.idp.repository.IdentityProviderEntityManager;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * JmsTemplateService
 *
 * @author jeff.a.schenk@gmail.com on 11/11/15.
 */
@Service
public class MessagePublisherServiceImpl implements MessagePublisherService {

    /**
     * Common Logger
     */
    protected final static org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(MessagePublisherService.class);

    /**
     * JMS Template used for Publishing, All Listeners are Annotated or are External are are reached
     * via a Broker Channel.
     */
    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * Identity Provider Entity Manager
     */
    @Autowired
    private IdentityProviderEntityManager identityProviderEntityManager;

    /**
     * Initialization of Service
     */
    @PostConstruct
    public void initialization() {
        LOGGER.info("Initialization of Messaging Publisher Service Implementation, successful.");
    }

    /**
     * Shutdown and Removal of Service.
     */
    @PreDestroy
    public void destroyBean() {
        LOGGER.info("Removing Messaging Publisher Service Implementation, shutdown in progress.");
    }

    /**
     * Get Jms Template
     * @return JmsTemplate
     */
    @Override
    public JmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }

    /**
     * publishPersonEventHistory
     * @param principalID ID of Authenticated User Principal.
     * @param yourEntityEventHistory to be Published.
     */
    @Override
    public void publishEntityEventHistory(Long principalID, YourEntityEventHistory yourEntityEventHistory) {
        if (principalID == null || yourEntityEventHistory == null) {
            return;
        }
        /**
         * Lookup the Entity to Associate.
         */
        YourEntity yourEntity = identityProviderEntityManager.findYourEntityById(principalID);
        if (yourEntity != null) {
            yourEntityEventHistory.setYourEntity(yourEntity);
            /**
             * Publish the Entity History Event.
             */
            getJmsTemplate().convertAndSend(SystemJMSLocalInstanceDestinations.YOUR_MS_ENTITY_EVENT_HISTORY_QUEUE,
                    yourEntityEventHistory);
            LOGGER.info("Entity Event History Tag:[{}] Published.", yourEntityEventHistory.getEventTagName());
        }
    }
}
