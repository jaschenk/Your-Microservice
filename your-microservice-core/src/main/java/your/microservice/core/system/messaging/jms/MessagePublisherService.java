package your.microservice.core.system.messaging.jms;

import org.springframework.jms.core.JmsTemplate;

/**
 * JmsTemplateService
 *
 * @author jeff.a.schenk@gmail.com on 11/11/15.
 */
public interface MessagePublisherService {

    JmsTemplate getJmsTemplate();
}
