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
import your.microservice.core.security.idp.model.base.YourEntityTokenHistory;
import your.microservice.core.security.idp.model.types.YourEntityTokenStatus;
import your.microservice.core.security.idp.repository.IdentityProviderEntityManager;

import java.time.Instant;
import java.util.*;

import static org.junit.Assert.*;

/**
 * IdPEMYourEntityTokenHistoryIT
 *
 * @author jeff.a.schenk@gmail.com on 2/21/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {MicroserviceTestApplication.class})
@WebIntegrationTest({"server.port:0", "test.environment.property:true"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IdPEMYourEntityTokenHistoryIT {
    /**
     * Common Logger
     */
    protected final static org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(IdPEMYourEntityTokenHistoryIT.class);

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


    @Test
    public void test01_TokenHistoryLifecycle() {
        LOGGER.info("Running: test01_TokenHistoryLifecycle");

        /**
         * Test creating a JTI
         */
        String jti = UUID.randomUUID().toString();
        YourEntityTokenHistory yourEntityTokenHistory = new YourEntityTokenHistory();
        yourEntityTokenHistory.setJti(jti);
        yourEntityTokenHistory.setSubject(USER_EMAIL);
        yourEntityTokenHistory.setUsageCount(1L);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 4);
        yourEntityTokenHistory.setExpiration(calendar.getTime());

        yourEntityTokenHistory.setIssuedAt(Date.from(Instant.now()));
        yourEntityTokenHistory.setLastUsed(Date.from(Instant.now()));
        yourEntityTokenHistory.setNotUsedBefore(Date.from(Instant.now()));
        yourEntityTokenHistory.setStatus(YourEntityTokenStatus.ACTIVE);

        yourEntityTokenHistory =
                identityProviderEntityManager.createTokenHistory(yourEntityTokenHistory);
        assertNotNull(yourEntityTokenHistory);


        for (int i = 2; i < 10; i++) {
            Integer updated = identityProviderEntityManager.incrementTokenHistoryUsage(jti);
            assertNotNull(updated);
            assertTrue(updated == 1);

            yourEntityTokenHistory =
                    identityProviderEntityManager.readTokenHistory(jti);
            assertNotNull(yourEntityTokenHistory);
            assertEquals(i, yourEntityTokenHistory.getUsageCount().longValue());
        }

        Integer updatedStatus = identityProviderEntityManager.updateTokenHistoryStatus(jti, YourEntityTokenStatus.REVOKED);
        assertNotNull(updatedStatus);
        assertTrue(updatedStatus == 1);

        updatedStatus = identityProviderEntityManager.updateTokenHistoryStatus(jti, YourEntityTokenStatus.PENDING);
        assertNotNull(updatedStatus);
        assertTrue(updatedStatus == 1);

        yourEntityTokenHistory =
                identityProviderEntityManager.readTokenHistory(jti);
        assertNotNull(yourEntityTokenHistory);
        assertEquals(YourEntityTokenStatus.PENDING, yourEntityTokenHistory.getStatus());

        assertTrue(identityProviderEntityManager.deleteTokenHistory(jti) == 1);

        List<YourEntityTokenHistory> history = identityProviderEntityManager.readCurrentExpiredTokenHistory();
        assertNotNull(history);
        assertEquals(0, history.size());

        history = identityProviderEntityManager.readCurrentNonExpiredTokenHistory();
        assertNotNull(history);
        assertEquals(0, history.size());

    }

    @Test
    public void test02_TokenHistoryExpiredTokens() {
        LOGGER.info("Running: test02_TokenHistoryExpiredTokens");
        assertNotNull(environment);

        assertNotNull(identityProviderEntityManager);

        assertEquals("true",
                environment.getProperty("test.environment.property"));

        /**
         * Test creating several Expired JWTs
         */
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -4);  // Expired 4 Minutes Ago.
        generateTokens(100, calendar.getTime());

        List<YourEntityTokenHistory> history = identityProviderEntityManager.readCurrentExpiredTokenHistory();
        assertNotNull(history);
        assertEquals(100, history.size());

        assertTrue(identityProviderEntityManager.deleteTokenHistory() == 100);

        history = identityProviderEntityManager.readCurrentExpiredTokenHistory();
        assertNotNull(history);
        assertEquals(0, history.size());

        history = identityProviderEntityManager.readCurrentNonExpiredTokenHistory();
        assertNotNull(history);
        assertEquals(0, history.size());

    }

    @Test
    public void test03_TokenHistoryDeleteByJTI() {
        LOGGER.info("Running: test03_TokenHistoryDeleteByJTI");

        /**
         * Test creating several Expired JWTs
         */
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 1);  // Expired 1 Minute from Now.
        List<String> jtis = generateTokens(100, calendar.getTime());

        List<YourEntityTokenHistory> history = identityProviderEntityManager.readCurrentNonExpiredTokenHistory();
        assertNotNull(history);
        assertEquals(100, history.size());

        for (String jti : jtis) {
            assertTrue(identityProviderEntityManager.deleteTokenHistory(jti) == 1);
        }

        history = identityProviderEntityManager.readCurrentExpiredTokenHistory();
        assertNotNull(history);
        assertEquals(0, history.size());

        history = identityProviderEntityManager.readCurrentNonExpiredTokenHistory();
        assertNotNull(history);
        assertEquals(0, history.size());

    }

    @Test
    public void test04_TokenHistoryDeleteBySUBJECT() {
        LOGGER.info("Running: test04_TokenHistoryDeleteBySUBJECT");

        /**
         * Test creating several Expired JWTs
         */
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 1);  // Expired 1 Minute from Now.
        List<String> jtis = generateTokens(100, calendar.getTime());

        List<YourEntityTokenHistory> history = identityProviderEntityManager.readTokenHistoryBySubject(USER_EMAIL);
        assertNotNull(history);
        assertEquals(100, history.size());

        assertTrue(identityProviderEntityManager.deleteTokenHistoryBySubject(USER_EMAIL) == 100);


        history = identityProviderEntityManager.readCurrentExpiredTokenHistory();
        assertNotNull(history);
        assertEquals(0, history.size());

        history = identityProviderEntityManager.readCurrentNonExpiredTokenHistory();
        assertNotNull(history);
        assertEquals(0, history.size());

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

    /**
     * Generate a Number of Tokens
     *
     * @param expiration Date these Tokens Expire
     * @return
     */
    private List<String> generateTokens(Integer numberToGenerate, Date expiration) {
        List<String> jtis = new ArrayList<>();
        for (int i = 0; i < numberToGenerate; i++) {
            String jti = UUID.randomUUID().toString();
            jtis.add(jti);
            YourEntityTokenHistory yourEntityTokenHistory = new YourEntityTokenHistory();
            yourEntityTokenHistory.setJti(jti);
            yourEntityTokenHistory.setSubject(USER_EMAIL);
            yourEntityTokenHistory.setUsageCount(1L);

            yourEntityTokenHistory.setExpiration(expiration);

            yourEntityTokenHistory.setIssuedAt(Date.from(Instant.now()));
            yourEntityTokenHistory.setLastUsed(Date.from(Instant.now()));
            yourEntityTokenHistory.setNotUsedBefore(Date.from(Instant.now()));
            yourEntityTokenHistory.setStatus(YourEntityTokenStatus.ACTIVE);

            yourEntityTokenHistory =
                    identityProviderEntityManager.createTokenHistory(yourEntityTokenHistory);
            assertNotNull(yourEntityTokenHistory);
        }
        return jtis;
    }

}
