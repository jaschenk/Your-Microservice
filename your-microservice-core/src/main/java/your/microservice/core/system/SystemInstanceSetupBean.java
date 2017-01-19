package your.microservice.core.system;

import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 *  SystemInstanceSetupBean
 */
@Component
public class SystemInstanceSetupBean implements ApplicationListener<ApplicationEvent> {
	/**
	 * Common Logger
	 */
	protected final static org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(SystemInstanceSetupBean.class);
	/**
	 * Integration Spring Boot Service Port.
	 */
	private Integer hostPortUsed;

	/**
	 * Trap applicable Application Events.
	 *
	 * @param applicationEvent Application Event to be processed.
	 */
	@Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {

		if (applicationEvent instanceof EmbeddedServletContainerInitializedEvent) {
			hostPortUsed = ((EmbeddedServletContainerInitializedEvent) applicationEvent).
					getEmbeddedServletContainer().getPort();
			LOGGER.info("onApplicationEvent Fired, System Port Identity:[{}]", hostPortUsed);
		} else if (applicationEvent instanceof ContextClosedEvent) {
			LOGGER.info("onApplicationEvent for ContextClosedEvent...");
		}
	}

	/**
	 * initialization
	 * Entered when Bean is initialized.
	 */
	@PostConstruct
	protected void initialization() {
		LOGGER.info("Initialization of Your Microservice System Instance Setup Bean has been wired into runtime Environment.");
	}

	/**
	 * destroyBean
	 * Entered when Bean is being destroyed or torn down from the runtime Environment.
	 * Simple indicate destruction...
	 */
	@PreDestroy
	protected void destroyBean() {
		LOGGER.info("Your Microservice System Instance Setup Bean Implementation has been removed from the runtime Environment.");
	}

	public Integer getHostPortUsed() {
		return hostPortUsed;
	}

}
