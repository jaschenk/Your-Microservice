package your.microservice.idp.service;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import your.microservice.MicroserviceTestApplication;
import your.microservice.idp.model.base.YourEntity;
import your.microservice.idp.repository.IdentityProviderEntityManager;
import your.microservice.idp.security.YourMicroserviceSecurityConstants;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * IdPEMYourEntityTokenHistoryIT
 *
 * @author jeff.a.schenk@gmail.com on 2/21/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {MicroserviceTestApplication.class})
@WebIntegrationTest({"server.port:0","test.environment.property:true"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IdPCredentialIT {
    /**
     * Common Logger
     */
    protected final static org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(IdPCredentialIT.class);

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
    private static final String CLEAR_TEXT_CREDENTIALS = "TestPassword";


    @Test
    public void test01_EncodeCredentials() {
        LOGGER.info("Running: test01_EncodeCredentials");
        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder(YourMicroserviceSecurityConstants.BCRYPT_STRENGTH_SETTING);
        String encryptedCredentials = encoder.encode(CLEAR_TEXT_CREDENTIALS);
        assertTrue(encoder.matches(CLEAR_TEXT_CREDENTIALS, encryptedCredentials));

        LOGGER.info("RAW:[{}], ENCRYPTED:[{}]", CLEAR_TEXT_CREDENTIALS, encryptedCredentials);
    }

    @Test
    public void test02_CheckCredentials() {
        LOGGER.info("Running: test02_CheckCredentials");
        YourEntity yourEntity = identityProviderEntityManager.findYourEntityByEmail(USER_EMAIL);
        assertNotNull(yourEntity);

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder(YourMicroserviceSecurityConstants.BCRYPT_STRENGTH_SETTING);
        assertTrue(encoder.matches(CLEAR_TEXT_CREDENTIALS, yourEntity.getCredentials()));
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
