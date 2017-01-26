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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import your.microservice.MicroserviceTestApplication;
import your.microservice.core.security.idp.model.base.YourEntityOrganization;
import your.microservice.core.security.idp.model.types.YourEntityStatus;
import your.microservice.core.security.idp.repository.IdentityProviderEntityManager;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * IdPEMYourEntityOrganizationIT
 *
 * @author jeff.a.schenk@gmail.com on 2/21/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {MicroserviceTestApplication.class})
@WebIntegrationTest({"server.port:0", "test.environment.property:true"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IdPEMYourEntityOrganizationIT {
    /**
     * Common Logger
     */
    protected final static org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(IdPEMYourEntityOrganizationIT.class);

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
    private static final String ENTITY_ORG_NAME = "Test Organization";
    private static final String TEST_NEW_ENTITY_ORG_NAME = "FooBar Entity Organization";


    @Test
    public void test01_YourEntityOrganization() {
        LOGGER.info("Running: test01_YourEntityOrganization");

        List<YourEntityOrganization> orgResults = identityProviderEntityManager.findAllYourEntityOrganizations();
        assertNotNull(orgResults);
        assertEquals(4, orgResults.size());
        for (int i = 0; i < orgResults.size(); i++) {
            LOGGER.info("Result[{}]: --> {}", i, orgResults.get(i).toString());
        }
    }

    @Test
    public void test02_FindYourEntityOrganizationById() {
        LOGGER.info("Running: test02_FindYourEntityOrganizationById");

        YourEntityOrganization yourEntityOrganization =
                identityProviderEntityManager.findYourEntityOrganizationById(1L);
        assertNotNull(yourEntityOrganization);
        assertEquals(ENTITY_ORG_NAME, yourEntityOrganization.getName());
    }

    @Test
    public void test03_FindYourEntityOrganizationByName() {
        LOGGER.info("Running: test03_FindYourEntityOrganizationByName");

        YourEntityOrganization yourEntityOrganization =
                identityProviderEntityManager.findYourEntityOrganizationByName(ENTITY_ORG_NAME);
        assertNotNull(yourEntityOrganization);
        assertEquals(ENTITY_ORG_NAME, yourEntityOrganization.getName());
    }

    @Test
    public void test04_CreateNewYourEntityOrganization() {
        LOGGER.info("Running: test04_CreateNewYourEntityOrganization");
        YourEntityOrganization yourEntityOrganization = new YourEntityOrganization();
        yourEntityOrganization.setName(TEST_NEW_ENTITY_ORG_NAME);
        yourEntityOrganization.setStatus(YourEntityStatus.ACTIVE);
        identityProviderEntityManager.saveYourEntityOrganization(yourEntityOrganization);

        yourEntityOrganization =
                identityProviderEntityManager.findYourEntityOrganizationByName(TEST_NEW_ENTITY_ORG_NAME);
        assertNotNull(yourEntityOrganization);
        assertEquals(TEST_NEW_ENTITY_ORG_NAME, yourEntityOrganization.getName());
    }

    @Test
    public void test05_YourEntityOrganization() {
        LOGGER.info("Running: test05_YourEntityOrganization");

        List<YourEntityOrganization> orgResults = identityProviderEntityManager.findAllYourEntityOrganizations();
        assertNotNull(orgResults);
        assertEquals(5, orgResults.size());
        for (int i = 0; i < orgResults.size(); i++) {
            LOGGER.info("Result[{}]: --> {}", i, orgResults.get(i).toString());
        }
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
