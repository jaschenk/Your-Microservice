package your.microservice.idp.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
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
import your.microservice.idp.model.base.YourEntityOrganization;
import your.microservice.idp.model.base.YourEntityRole;
import your.microservice.idp.model.base.YourEntityTokenHistory;
import your.microservice.idp.model.types.YourEntityStatus;
import your.microservice.idp.model.types.YourEntityTokenStatus;
import your.microservice.testutil.IntegrationTestSetupBean;

import java.time.Instant;
import java.util.*;

import static org.junit.Assert.*;

/**
 * IdentityProviderEntityManagerIT
 *
 * @author jeff.a.schenk@gmail.com on 2/21/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {MicroserviceTestApplication.class})
@WebIntegrationTest({"server.port:0","test.environment.property:true"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IdentityProviderEntityManagerIT {
    /**
     * Common Logger
     */
    protected final static org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(IdentityProviderEntityManagerIT.class);

    /**
     * Environment
     */
    @Autowired
    private Environment environment;

    /**
     * Test Integration Helper Setup Bean
     */
    @Autowired
    private IntegrationTestSetupBean integrationTestSetupBean;

    /**
     * Identity Provider Entity Manager
     */
    @Autowired
    private IdentityProviderEntityManager identityProviderEntityManager;

    /**
     * Test Constants
     */
    private static final String USER_EMAIL = "joe.user@mail.com";
    private static final String ENTITY_ORG_NAME = "Test Organization";
    private static final String ENTITY_ROLE_NAME = "USER";


    @Test
    public void test01_TokenHistoryLifecycle() {
        LOGGER.info("Running: test01_TokenHistoryLifecycle");

        List<YourEntity> results = identityProviderEntityManager.findAllYourEntities();
        assertNotNull(results);
        assertEquals(2, results.size());
        LOGGER.info("Result[0]: --> {}", results.get(0).toString());
        LOGGER.info("Result[1]: --> {}", results.get(1).toString());

        YourEntity yourEntity = identityProviderEntityManager.findYourEntityByEmail(USER_EMAIL);
        assertNotNull(yourEntity);
        assertEquals(USER_EMAIL, yourEntity.getEntityEmailAddress());



        List<YourEntityOrganization> orgResults = identityProviderEntityManager.findAllYourEntityOrganizations();
        assertNotNull(orgResults);
        assertEquals(4, orgResults.size());
        for(int i=0;i<orgResults.size();i++) {
            LOGGER.info("Result[{}]: --> {}", i, orgResults.get(i).toString());
        }


        YourEntityOrganization yourEntityOrganization =
                identityProviderEntityManager.findYourEntityOrganizationByName(ENTITY_ORG_NAME);
        assertNotNull(yourEntityOrganization);
        assertEquals(ENTITY_ORG_NAME, yourEntityOrganization.getName());


        List<YourEntityRole> roleResults = identityProviderEntityManager.findAllYourEntityRoles();
        assertNotNull(roleResults);
        assertEquals(3, roleResults.size());
        for(int i=0;i<roleResults.size();i++) {
            LOGGER.info("Result[{}]: --> {}", i, orgResults.get(i).toString());
        }


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


        for(int i=2; i<10;i++) {
            Integer updated = identityProviderEntityManager.incrementTokenHistoryUsage(jti);
            assertNotNull(updated);
            assertTrue(updated==1);

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

        assertTrue( identityProviderEntityManager.deleteTokenHistory(jti) == 1);

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
        for(int i = 0; i<100;i++) {
            String jti = UUID.randomUUID().toString();
            YourEntityTokenHistory yourEntityTokenHistory = new YourEntityTokenHistory();
            yourEntityTokenHistory.setJti(jti);
            yourEntityTokenHistory.setSubject(USER_EMAIL);
            yourEntityTokenHistory.setUsageCount(1L);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, -4);  // Expired 4 Minutes Ago.
            yourEntityTokenHistory.setExpiration(calendar.getTime());

            yourEntityTokenHistory.setIssuedAt(Date.from(Instant.now()));
            yourEntityTokenHistory.setLastUsed(Date.from(Instant.now()));
            yourEntityTokenHistory.setNotUsedBefore(Date.from(Instant.now()));
            yourEntityTokenHistory.setStatus(YourEntityTokenStatus.ACTIVE);

            yourEntityTokenHistory =
                identityProviderEntityManager.createTokenHistory(yourEntityTokenHistory);
            assertNotNull(yourEntityTokenHistory);
        }

        List<YourEntityTokenHistory> history = identityProviderEntityManager.readCurrentExpiredTokenHistory();
        assertNotNull(history);
        assertEquals(100, history.size());

        assertTrue(identityProviderEntityManager.deleteTokenHistory()==100);

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
        List<String> jtis = new ArrayList<>();
        for(int i = 0; i<100;i++) {
            String jti = UUID.randomUUID().toString();
            jtis.add(jti);
            YourEntityTokenHistory yourEntityTokenHistory = new YourEntityTokenHistory();
            yourEntityTokenHistory.setJti(jti);
            yourEntityTokenHistory.setSubject(USER_EMAIL);
            yourEntityTokenHistory.setUsageCount(1L);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 1);  // Expired 1 Minute from Now.
            yourEntityTokenHistory.setExpiration(calendar.getTime());

            yourEntityTokenHistory.setIssuedAt(Date.from(Instant.now()));
            yourEntityTokenHistory.setLastUsed(Date.from(Instant.now()));
            yourEntityTokenHistory.setNotUsedBefore(Date.from(Instant.now()));
            yourEntityTokenHistory.setStatus(YourEntityTokenStatus.ACTIVE);

            yourEntityTokenHistory =
                    identityProviderEntityManager.createTokenHistory(yourEntityTokenHistory);
            assertNotNull(yourEntityTokenHistory);
        }

        List<YourEntityTokenHistory> history = identityProviderEntityManager.readCurrentNonExpiredTokenHistory();
        assertNotNull(history);
        assertEquals(100, history.size());

        for(String jti:jtis) {
            assertTrue(identityProviderEntityManager.deleteTokenHistory(jti)==1);
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
        List<String> jtis = new ArrayList<>();
        for(int i = 0; i<100;i++) {
            String jti = UUID.randomUUID().toString();
            jtis.add(jti);
            YourEntityTokenHistory yourEntityTokenHistory = new YourEntityTokenHistory();
            yourEntityTokenHistory.setJti(jti);
            yourEntityTokenHistory.setSubject(USER_EMAIL);
            yourEntityTokenHistory.setUsageCount(1L);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 1);  // Expired 1 Minute from Now.
            yourEntityTokenHistory.setExpiration(calendar.getTime());

            yourEntityTokenHistory.setIssuedAt(Date.from(Instant.now()));
            yourEntityTokenHistory.setLastUsed(Date.from(Instant.now()));
            yourEntityTokenHistory.setNotUsedBefore(Date.from(Instant.now()));
            yourEntityTokenHistory.setStatus(YourEntityTokenStatus.ACTIVE);

            yourEntityTokenHistory =
                    identityProviderEntityManager.createTokenHistory(yourEntityTokenHistory);
            assertNotNull(yourEntityTokenHistory);
        }

        List<YourEntityTokenHistory> history = identityProviderEntityManager.readTokenHistoryBySubject(USER_EMAIL);
        assertNotNull(history);
        assertEquals(100, history.size());

        assertTrue(identityProviderEntityManager.deleteTokenHistoryBySubject(USER_EMAIL)==100);


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

}
