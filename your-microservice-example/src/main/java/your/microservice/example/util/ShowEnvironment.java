package your.microservice.example.util;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * ShowEnvironment
 *
 * @author jeff.a.schenk@gmail.com on 12/9/15.
 */
@Component
public class ShowEnvironment {

    /**
     * Common Logger
     */
    protected final static org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(ShowEnvironment.class);

    @Value("${your.microservice.app.runtime.environment}")
    private String environmentName;

    @PostConstruct
    public void initialization() {
        LOGGER.info("Assembly RBAC Authorization Groups from IDaaS for Runtime Environment:[{}]", environmentName);
    }
}
