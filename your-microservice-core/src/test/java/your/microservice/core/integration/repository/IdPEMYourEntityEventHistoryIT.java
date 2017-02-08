package your.microservice.core.integration.repository;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import your.microservice.MicroserviceTestApplication;
import your.microservice.core.security.idp.model.base.YourEntity;
import your.microservice.core.security.idp.model.base.YourEntityEventHistory;
import your.microservice.core.security.idp.repository.IdentityProviderEntityManager;
import your.microservice.testutil.IntegrationTestSetupBean;

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

    @Test
    public void test01_CreateSomeEventHistoryActivities() {
        LOGGER.info("Running: test01_CreateSomeEventHistoryActivities");

        /**
         * First clear our Event History.
         */
        Integer count = identityProviderEntityManager.deleteEventHistory();
        assertNotNull(count);
        LOGGER.info("Event History Entities Deleted:[{}]", count);

        /**
         * Obtain a Test YourEntity ...
         */
        YourEntity yourEntity = identityProviderEntityManager.findYourEntityByEmail(IntegrationTestSetupBean.ADMIN_EMAIL);
        assertNotNull(yourEntity);
        assertEquals(IntegrationTestSetupBean.ADMIN_EMAIL, yourEntity.getEntityEmailAddress());

        Map<String, String> eventProperties = new HashMap<>();
        eventProperties.put("ZERO_KEY", "ZERO_VALUE");

        for (int i = 0; i < 100; i++) {
            YourEntityEventHistory eventHistory =
                    new YourEntityEventHistory(yourEntity, IntegrationTestSetupBean.EVENT_TAG_NAME,
                            IntegrationTestSetupBean.EVENT_MESSAGE, eventProperties);
            identityProviderEntityManager.createEventHistory(eventHistory);
        }

        List<YourEntityEventHistory> results =
                identityProviderEntityManager.findAllYourEntityEventHistory(yourEntity.getEntityId());
        assertNotNull(results);
        assertEquals(100, results.size());

        results = identityProviderEntityManager.findAllYourEntityEventHistory();
        assertNotNull(results);
        assertEquals(100, results.size());

    }

    @Test
    public void test02_ClearAllEventHistory() {
        LOGGER.info("Running: test02_ClearAllEventHistory");

        /**
         * First clear our Event History.
         */
        Integer count = identityProviderEntityManager.deleteEventHistory();
        assertNotNull(count);
        LOGGER.info("Event History Entities Deleted:[{}]", count);

        /**
         * Validate Event History Empty.
         */
        List<YourEntityEventHistory> results = identityProviderEntityManager.findAllYourEntityEventHistory();
        assertNotNull(results);
        assertEquals(0, results.size());
    }


    @Test
    public void test03_EventHistoryLifeCycle() {
        LOGGER.info("Running: test03_EventHistoryLifeCycle");

        /**
         * Obtain a Test YourEntity ...
         */
        YourEntity yourEntity = identityProviderEntityManager.findYourEntityByEmail(IntegrationTestSetupBean.USER_EMAIL);
        assertNotNull(yourEntity);
        assertEquals(IntegrationTestSetupBean.USER_EMAIL, yourEntity.getEntityEmailAddress());

        Map<String, String> eventProperties = new HashMap<>();
        eventProperties.put("ONE_KEY", "ONE_VALUE");

        YourEntityEventHistory eventHistory =
                new YourEntityEventHistory(yourEntity, IntegrationTestSetupBean.EVENT_TAG_NAME,
                        IntegrationTestSetupBean.EVENT_MESSAGE, eventProperties);
        identityProviderEntityManager.createEventHistory(eventHistory);


        List<YourEntityEventHistory> results =
                identityProviderEntityManager.findAllYourEntityEventHistory(yourEntity.getEntityId());
        assertNotNull(results);
        assertEquals(1, results.size());
        YourEntityEventHistory eventHistory_1 = results.get(0);
        assertEquals(eventHistory.getId(), eventHistory_1.getId());
        assertEquals(IntegrationTestSetupBean.EVENT_TAG_NAME, eventHistory_1.getEventTagName());
        assertEquals(IntegrationTestSetupBean.EVENT_MESSAGE, eventHistory_1.getEventMessage());
        assertNotNull(eventHistory_1.getEventTagProperties());
        assertEquals("ONE_VALUE", eventHistory_1.getEventTagProperties().get("ONE_KEY"));

        results = identityProviderEntityManager.findAllYourEntityEventHistory();
        assertNotNull(results);
        assertEquals(1, results.size());

    }

    @Test
    public void test04_MoreEventHistory() {
        LOGGER.info("Running: test04_MoreEventHistory");

        List<YourEntityEventHistory> results = identityProviderEntityManager.findAllYourEntityEventHistory();
        assertNotNull(results);
        assertEquals(1, results.size());

        /**
         * Obtain a Test YourEntity ...
         */
        YourEntity yourEntity = identityProviderEntityManager.findYourEntityByEmail(IntegrationTestSetupBean.USER_EMAIL);
        assertNotNull(yourEntity);
        assertEquals(IntegrationTestSetupBean.USER_EMAIL, yourEntity.getEntityEmailAddress());

        Map<String, String> eventProperties = new HashMap<>();
        eventProperties.put("TWO_KEY", "TWO_VALUE");

        for (int i = 0; i < 100; i++) {

            YourEntityEventHistory eventHistory =
                    new YourEntityEventHistory(yourEntity, IntegrationTestSetupBean.EVENT_TAG_NAME,
                            IntegrationTestSetupBean.EVENT_MESSAGE, eventProperties);
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
