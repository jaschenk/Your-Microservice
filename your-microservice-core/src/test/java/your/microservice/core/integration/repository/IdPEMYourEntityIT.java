package your.microservice.core.integration.repository;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import your.microservice.MicroserviceTestApplication;
import your.microservice.core.security.idp.model.base.YourEntity;
import your.microservice.core.security.idp.model.types.YourEntityStatus;
import your.microservice.core.security.idp.repository.IdentityProviderEntityManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static your.microservice.testutil.IntegrationTestSetupBean.USER_EMAIL;
import static your.microservice.testutil.IntegrationTestSetupBean.USER_EMAIL_101;

/**
 * IdPEMYourEntityTokenHistoryIT
 *
 * @author jeff.a.schenk@gmail.com on 2/21/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {MicroserviceTestApplication.class})
@WebIntegrationTest({"server.port:0", "test.environment.property:true"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IdPEMYourEntityIT {
    /**
     * Common Logger
     */
    protected final static org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(IdPEMYourEntityIT.class);

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
    public void test01_GetYourEntities() {
        LOGGER.info("Running: test01_GetYourEntities");

        List<YourEntity> results = identityProviderEntityManager.findAllYourEntities();
        assertNotNull(results);
        assertEquals(3, results.size());   // Previously Loaded ...
        LOGGER.info("Result[0]: --> {}", results.get(0).toString());
        LOGGER.info("Result[1]: --> {}", results.get(1).toString());
        LOGGER.info("Result[2]: --> {}", results.get(2).toString());

        YourEntity yourEntity = identityProviderEntityManager.findYourEntityByEmail(USER_EMAIL);
        assertNotNull(yourEntity);
        assertEquals(USER_EMAIL, yourEntity.getEntityEmailAddress());

    }

    @Test(expected = JpaSystemException.class)
    public void test02_CreateYourEntity_DuplicateEntry() {
        LOGGER.info("Running: test02_CreateYourEntity");
        YourEntity yourEntity = new YourEntity();
        yourEntity.setEntityEmailAddress(USER_EMAIL);  // Will Fail since it is not Unique ...
        yourEntity.setCredentials("password");
        yourEntity.setEntityGivenName("Ro");
        yourEntity.setEntitySurname("Bot");
        yourEntity.setStatus(YourEntityStatus.ACTIVE);
        yourEntity.setYourEntityRoles(new HashSet<>());
        yourEntity.setEntityProperties(new HashMap<>());
        yourEntity.setYourEntityOrganizations(new HashSet<>());

        identityProviderEntityManager.saveYourEntity(yourEntity);
    }

    @Test
    public void test03_CreateYourEntity() {
        LOGGER.info("Running: test03_CreateYourEntity");
        YourEntity yourEntity = new YourEntity();
        yourEntity.setEntityEmailAddress(USER_EMAIL_101);
        yourEntity.setCredentials("password");
        yourEntity.setEntityGivenName("Foo");
        yourEntity.setEntitySurname("Bar");
        yourEntity.setStatus(YourEntityStatus.ACTIVE);
        yourEntity.setYourEntityRoles(new HashSet<>());
        yourEntity.setEntityProperties(new HashMap<>());
        yourEntity.setYourEntityOrganizations(new HashSet<>());

        identityProviderEntityManager.saveYourEntity(yourEntity);

        yourEntity = identityProviderEntityManager.findYourEntityByEmail(USER_EMAIL_101);
        assertNotNull(yourEntity);
        assertEquals(USER_EMAIL_101, yourEntity.getEntityEmailAddress());
        assertEquals("Foo", yourEntity.getEntityGivenName());
        assertEquals("Bar", yourEntity.getEntitySurname());
        assertEquals(YourEntityStatus.ACTIVE, yourEntity.getStatus());
        assertNotNull(yourEntity.getEntityId());
        assertNotNull(yourEntity.getYourEntityRoles());
        assertNotNull(yourEntity.getEntityProperties());
        assertNotNull(yourEntity.getYourEntityOrganizations());

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
