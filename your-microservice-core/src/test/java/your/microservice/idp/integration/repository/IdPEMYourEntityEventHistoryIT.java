package your.microservice.idp.integration.repository;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import your.microservice.MicroserviceTestApplication;
import your.microservice.idp.model.base.YourEntity;
import your.microservice.idp.model.base.YourEntityEventHistory;
import your.microservice.idp.repository.IdentityProviderEntityManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * IdPEMYourEntityEventHistoryIT
 *
 * @author jeff.a.schenk@gmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {MicroserviceTestApplication.class})
@WebIntegrationTest({"server.port:0", "test.environment.property:true"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IdPEMYourEntityEventHistoryIT {
    /**
     * Common Logger
     */
    protected final static org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(IdPEMYourEntityEventHistoryIT.class);

    /**
     * Environment
     */
    @Autowired
    private Environment environment;

    /**
     * Identity Provider Entity Manager
     */
    @Autowired
    private IdentityProviderEntityManager identityProviderEntityManager;

    /**
     * Test Constants
     */
    private static final String USER_EMAIL = "joe.user@mail.com";

    private static final String EVENT_TAG_NAME = "EVENT_TAG_NAME";
    private static final String EVENT_MESSAGE = "Test Event Message";


    @Test
    public void test01_EventHistory() {
        LOGGER.info("Running: test01_EventHistory");

        List<YourEntityEventHistory> results = identityProviderEntityManager.findAllYourEntityEventHistory();
        assertNotNull(results);
        assertEquals(0, results.size());

        /**
         * Obtain a Test YourEntity ...
         */
        YourEntity yourEntity = identityProviderEntityManager.findYourEntityByEmail(USER_EMAIL);
        assertNotNull(yourEntity);
        assertEquals(USER_EMAIL, yourEntity.getEntityEmailAddress());

        Map<String, String> eventProperties = new HashMap<>();
        eventProperties.put("ONE_KEY", "ONE_VALUE");

        YourEntityEventHistory eventHistory =
                new YourEntityEventHistory(yourEntity, EVENT_TAG_NAME, EVENT_MESSAGE, eventProperties);
        identityProviderEntityManager.createEventHistory(eventHistory);


        results = identityProviderEntityManager.findAllYourEntityEventHistory(yourEntity.getEntityId());
        assertNotNull(results);
        assertEquals(1, results.size());
        YourEntityEventHistory eventHistory_1 = results.get(0);
        assertEquals(eventHistory.getId(), eventHistory_1.getId());
        assertEquals(EVENT_TAG_NAME, eventHistory_1.getEventTagName());
        assertEquals(EVENT_MESSAGE, eventHistory_1.getEventMessage());
        assertNotNull(eventHistory_1.getEventTagProperties());
        assertEquals("ONE_VALUE", eventHistory_1.getEventTagProperties().get("ONE_KEY"));

        results = identityProviderEntityManager.findAllYourEntityEventHistory();
        assertNotNull(results);
        assertEquals(1, results.size());

    }

    @Test
    public void test02_EventHistory() {
        LOGGER.info("Running: test02_EventHistory");

        List<YourEntityEventHistory> results = identityProviderEntityManager.findAllYourEntityEventHistory();
        assertNotNull(results);
        assertEquals(1, results.size());

        /**
         * Obtain a Test YourEntity ...
         */
        YourEntity yourEntity = identityProviderEntityManager.findYourEntityByEmail(USER_EMAIL);
        assertNotNull(yourEntity);
        assertEquals(USER_EMAIL, yourEntity.getEntityEmailAddress());

        Map<String, String> eventProperties = new HashMap<>();
        eventProperties.put("TWO_KEY", "TWO_VALUE");

        for (int i = 0; i < 100; i++) {

            YourEntityEventHistory eventHistory =
                    new YourEntityEventHistory(yourEntity, EVENT_TAG_NAME, EVENT_MESSAGE, eventProperties);
            identityProviderEntityManager.createEventHistory(eventHistory);
        }


        results = identityProviderEntityManager.findAllYourEntityEventHistory(yourEntity.getEntityId());
        assertNotNull(results);
        assertEquals(101, results.size());

        results = identityProviderEntityManager.findAllYourEntityEventHistory();
        assertNotNull(results);
        assertEquals(101, results.size());

    }


    @Test
    public void test00_first() {
        LOGGER.info("Running: test00_first --> Should be First Test Run in Integration Test Suite");
        assertNotNull(environment);

        assertNotNull(identityProviderEntityManager);

        assertEquals("true",
                environment.getProperty("test.environment.property"));

    }

    @Test
    public void test99_last() {
        LOGGER.info("Running: test99_last --> Should be Last Test Run in Integration Test Suite");
    }

}
