package your.microservice.core.system.messaging.jms;

import org.springframework.jms.core.JmsTemplate;
import your.microservice.core.security.idp.model.base.YourEntityEventHistory;

/**
 * JmsTemplateService
 *
 * @author jeff.a.schenk@gmail.com.
 */
public interface MessagePublisherService {

    JmsTemplate getJmsTemplate();

    void publishEntityEventHistory(Long entityId, YourEntityEventHistory yourEntityEventHistory);
}
