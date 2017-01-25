package your.microservice.testutil;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import your.microservice.core.system.SystemInstanceSetupBean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * IntegrationTestSetupBean
 *
 * @author jeff.a.schenk@gmail.com on 7/29/15.
 */
@Component
public class IntegrationTestSetupBean {
    /**
     * Common Logger
     */
    protected final static org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(IntegrationTestSetupBean.class);

    /**
     * Constants
     */
    public static final String APPLICATION_JSON_WITH_UTF8_ENCODING_VALUE = "application/json;charset=UTF-8";

    /**
     * Test Data Constants
     */
    /**
     * Test Constants
     */
    public static final String USER_EMAIL = "joe.user@mail.com";
    public static final String CLEAR_TEXT_CREDENTIALS = "TestPassword";

    /**
     * System Instance Setup Bean, to obtain the Port we were booted on.
     */
    @Autowired
    private SystemInstanceSetupBean systemInstanceSetupBean;

    /**
     * initialization
     * Entered when Bean is initialized.
     */
    @PostConstruct
    protected void initialization() {
        LOGGER.info("Initialization of Integration Test Suite Setup has been wired into runtime Environment.");
    }

    /**
     * destroyBean
     * Entered when Bean is being destroyed or torn down from the runtime Environment.
     * Simple indicate destruction...
     */
    @PreDestroy
    protected void destroyBean() {
        LOGGER.info("Integration Test Suite Implementation has been removed from the runtime Environment.");
    }

    /**
     * Obtain the Port Used for this Bootstrap.
     *
     * @return Integer of Port Used, could be null.
     */
    public Integer getHostPortUsed() {
        if (systemInstanceSetupBean == null) {
            LOGGER.warn("systemInstanceSetupBean is currently Null at this Interval in the StartUp Process, unable to" +
                    "determine runtime Port!");
            return null;
        } else {
            return systemInstanceSetupBean.getHostPortUsed();
        }
    }

    /**
     * getHostPath
     *
     * @return String of constructed Host Path with our Integration Port Set.
     */
    public String getHostPath() {
        return "http://localhost:" + getHostPortUsed();
    }

    /**
     * Get absolute Path for Request Context and Relative Path
     *
     * @param serverContext Reference
     * @param relativePath  Reference
     * @return String construct URL Endpoint.
     */
    public String getAbsolutePath(final String serverContext, final String relativePath) {
        if (serverContext == null || serverContext.isEmpty()) {
            return String.format("http://%s:%d/%s/%s", "localhost", getHostPortUsed(), "testservice", relativePath);
        }
        return String.format("http://%s:%d/%s/%s", "localhost", getHostPortUsed(), serverContext, relativePath);
    }

    /**
     * Get absolute Path for Request Context and Relative Path
     *
     * @param relativePath Reference
     * @return String construct URL Endpoint.
     */
    public String getAbsolutePath(final String relativePath) {
        return String.format("http://%s:%d/%s", "localhost", getHostPortUsed(), relativePath);
    }

}
