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
import your.microservice.idp.model.base.YourEntityRole;
import your.microservice.idp.model.types.YourEntityStatus;
import your.microservice.idp.repository.IdentityProviderEntityManager;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * IdPEMYourEntityRoleIT
 *
 * @author jeff.a.schenk@gmail.com on 2/21/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {MicroserviceTestApplication.class})
@WebIntegrationTest({"server.port:0", "test.environment.property:true"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IdPEMYourEntityRoleIT {
    /**
     * Common Logger
     */
    protected final static org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(IdPEMYourEntityRoleIT.class);

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
    private static final String ENTITY_ROLE_NAME = "USER";


    @Test
    public void test01_GetAllExistingYourEntityRoles() {
        LOGGER.info("Running: test01_GetAllExistingYourEntityRoles");

        List<YourEntityRole> roleResults = identityProviderEntityManager.findAllYourEntityRoles();
        assertNotNull(roleResults);
        assertEquals(3, roleResults.size());
        for (int i = 0; i < roleResults.size(); i++) {
            LOGGER.info("Result[{}]: --> {}", i, roleResults.get(i).toString());
        }

    }

    @Test
    public void test02_YourEntityRole() {
        LOGGER.info("Running: test02_YourEntityRole");

        YourEntityRole yourEntityRole =
                identityProviderEntityManager.findYourEntityRoleByName(ENTITY_ROLE_NAME);
        assertNotNull(yourEntityRole);
        assertEquals(ENTITY_ROLE_NAME, yourEntityRole.getName());

        YourEntityRole yourEntityRole2 = new YourEntityRole();
        yourEntityRole2.setName("NEW_ROLE_NAME");
        yourEntityRole2.setStatus(YourEntityStatus.ACTIVE);
        identityProviderEntityManager.saveYourEntityRole(yourEntityRole2);

        yourEntityRole =
                identityProviderEntityManager.findYourEntityRoleByName("NEW_ROLE_NAME");
        assertNotNull(yourEntityRole);
        assertEquals("NEW_ROLE_NAME", yourEntityRole.getName());

    }

    @Test
    public void test03_GetAllExistingYourEntityRoles() {
        LOGGER.info("Running: test03_GetAllExistingYourEntityRoles");

        List<YourEntityRole> roleResults = identityProviderEntityManager.findAllYourEntityRoles();
        assertNotNull(roleResults);
        assertEquals(4, roleResults.size());   // Previous Test added One!
        for (int i = 0; i < roleResults.size(); i++) {
            LOGGER.info("Result[{}]: --> {}", i, roleResults.get(i).toString());
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
