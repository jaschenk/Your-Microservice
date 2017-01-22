package your.microservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import your.microservice.core.dm.dto.system.YourPulse;
import your.microservice.core.rest.RestClientAccessObject;
import your.microservice.core.rest.RestClientAccessor;
import your.microservice.core.rest.exceptions.NotAuthenticatedException;
import your.microservice.core.rest.exceptions.RestClientAccessorException;
import your.microservice.core.system.messaging.model.YourMSBulletinBroadcastNotification;
import your.microservice.idp.model.base.YourEntity;
import your.microservice.idp.model.base.YourEntityOrganization;
import your.microservice.idp.model.base.YourEntityRole;
import your.microservice.idp.model.base.YourEntityTokenHistory;
import your.microservice.idp.model.types.YourEntityTokenStatus;
import your.microservice.idp.repository.IdentityProviderEntityManager;
import your.microservice.testutil.IntegrationTestSetupBean;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

/**
 * EnableMicroserviceIT
 *
 * @author jeff.a.schenk@gmail.com on 2/21/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {MicroserviceTestApplication.class})
@WebIntegrationTest({"server.port:0","test.environment.property:true"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EnableMicroserviceIT {
    /**
     * Common Logger
     */
    protected final static org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(EnableMicroserviceIT.class);

    /**
     * Environment
     */
    @Autowired
    private Environment environment;
    /**
     * Web Application Context
     */
    @Autowired
    private WebApplicationContext webApplicationContext;
    /**
     * CMS Integration Helper Setup Bean
     */
    @Autowired
    private IntegrationTestSetupBean integrationTestSetupBean;

    /**
     * ObjectMapper
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * RESTful Client Accessor Service.
     */
    @Autowired
    private RestClientAccessor restClientAccessor;

    @Autowired
    private IdentityProviderEntityManager identityProviderEntityManager;

    /**
     * Test Constants
     */
    private static final String USER_EMAIL = "joe.user@mail.com";
    private static final String CREDENTIALS = "TestPassword";
    private static final String ENTITY_ORG_NAME = "Test Organization";
    private static final String ENTITY_ROLE_NAME = "USER";
    private static final String SERVICE_NAME = "testmicroservice";

    private static final String APIINFO_ENDPOINT = "/api/"+SERVICE_NAME+"/v1";
    private static final String BULLETIN_ENPOINT = "/api/"+SERVICE_NAME+"/v1/system/bulletin";
    private static final String PULSE_ENDPOINT = "/api/"+SERVICE_NAME+"/v1/system/pulse";
    private static final String LOGOUT_PREFIX_PATH = "/api/v1";
    private static final String TEST_ENDPOINT = "/api/"+SERVICE_NAME+"/v1/test";

    /**
     * Before each Test perform Mock Reset Assured Setup...
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        RestAssuredMockMvc.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void test01_EnvironmentSetup() {
        LOGGER.info("Running: test01_EnvironmentSetup");
        assertNotNull(environment);

        assertNotNull(identityProviderEntityManager);

        assertEquals("true",
               environment.getProperty("test.environment.property"));


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
            Boolean updated = identityProviderEntityManager.incrementTokenHistoryUsage(jti);
            assertNotNull(updated);
            assertTrue(updated);

            yourEntityTokenHistory =
                identityProviderEntityManager.readTokenHistory(jti);
            assertNotNull(yourEntityTokenHistory);
            assertEquals(i, yourEntityTokenHistory.getUsageCount().longValue());
        }
    }

    @Test
    public void test02_getAppInfo() {
        LOGGER.info("Running: test02_getAppInfo...");
        /**
         * Access Open End Point to Obtain API Information.
         */
       given().
                        header("Accept-Encoding", "application/json").
                        when().
                        get(integrationTestSetupBean.getHostPath()
                                + APIINFO_ENDPOINT).
                        then().
                        assertThat().statusCode(HttpStatus.SC_OK).
                        assertThat().contentType(IntegrationTestSetupBean.APPLICATION_JSON_WITH_UTF8_ENCODING_VALUE).
                        assertThat().body(containsString("\"description\":\"Your Commons MicroServices\"")).
                        assertThat().body(containsString("\"version\":\"")).
                        log().all().
                        extract().asString();
    }

    @Test
    public void test03_getAccessToken() {
        LOGGER.info("Running: test03_getAccessToken...");
        assertNotNull(restClientAccessor);
        RestClientAccessObject restClientAccessObject =
                restClientAccessor.getAccessToken(integrationTestSetupBean.getHostPath(), USER_EMAIL, CREDENTIALS);
        assertNotNull(restClientAccessObject);

        LOGGER.debug(" Access Token: [{}]", restClientAccessObject.getAccessToken());
        LOGGER.debug("Refresh Token: [{}]", restClientAccessObject.getRefreshToken());
        LOGGER.debug("   Token Type: [{}]", restClientAccessObject.getTokenType());
        LOGGER.debug("Token Expires: [{}]", restClientAccessObject.getExpires());

        int rc =
            restClientAccessor.logout(integrationTestSetupBean.getHostPath()+LOGOUT_PREFIX_PATH,
                    restClientAccessObject);
        assertEquals(204, rc);

    }

    @Test
    public void test04_getAccessTokenViaRefreshToken() {
        LOGGER.info("Running: test04_getAccessToken...");
        assertNotNull(restClientAccessor);
        RestClientAccessObject restClientAccessObject =
                restClientAccessor.getAccessToken(integrationTestSetupBean.getHostPath(), USER_EMAIL, CREDENTIALS);
        assertNotNull(restClientAccessObject);

        LOGGER.debug(" Access Token: [{}]", restClientAccessObject.getAccessToken());
        LOGGER.debug("Refresh Token: [{}]", restClientAccessObject.getRefreshToken());
        LOGGER.debug("   Token Type: [{}]", restClientAccessObject.getTokenType());
        LOGGER.debug("Token Expires: [{}]", restClientAccessObject.getExpires());


        restClientAccessObject =
                restClientAccessor.getAccessToken(integrationTestSetupBean.getHostPath(), restClientAccessObject);
        assertNotNull(restClientAccessObject);

        LOGGER.debug(" New Access Token: [{}]", restClientAccessObject.getAccessToken());
        LOGGER.debug("New Refresh Token: [{}]", restClientAccessObject.getRefreshToken());
        LOGGER.debug("   New Token Type: [{}]", restClientAccessObject.getTokenType());
        LOGGER.debug("New Token Expires: [{}]", restClientAccessObject.getExpires());

        int rc =
                restClientAccessor.logout(integrationTestSetupBean.getHostPath()+LOGOUT_PREFIX_PATH,
                        restClientAccessObject);
        assertEquals(204, rc);

    }

    @Test(expected = NotAuthenticatedException.class)
    public void test05_getAccessToken() {
        LOGGER.info("Running: test05_getAccessToken...");
        assertNotNull(restClientAccessor);
        restClientAccessor.getAccessToken(integrationTestSetupBean.getHostPath(),
                USER_EMAIL, "FOOBAR");

        fail("Should have failed with a 'Not Authenticated Exception', " +
                "as we present an Invalid Credentials, which should have been rejected," +
                " to access a Protected Resource, Very Bad!");
    }

    @Test
    public void test06_accessBulletin() {
        LOGGER.info("Running: test06_accessBulletin...");
        assertNotNull(restClientAccessor);
        /**
         * Authentication and Obtain Access Token.
         */
        RestClientAccessObject restClientAccessObject =
                restClientAccessor.getAccessToken(integrationTestSetupBean.getHostPath(), USER_EMAIL, CREDENTIALS);
        assertNotNull(restClientAccessObject);
        /**
         * Get Resource.
         */
        try {
            byte[] results = (byte[]) restClientAccessor.get(
                    integrationTestSetupBean.getHostPath()
                            + BULLETIN_ENPOINT, restClientAccessObject);
            YourMSBulletinBroadcastNotification yourMSBulletinBroadcastNotification
                    = objectMapper.readValue(results, YourMSBulletinBroadcastNotification.class);
            assertNotNull(yourMSBulletinBroadcastNotification);
            /**
             * Iterate over Properties
             */
            LOGGER.info("{}", yourMSBulletinBroadcastNotification);
        } catch (IOException ioe) {
            LOGGER.error("IOException Encountered: {}", ioe.getMessage(), ioe);
        }
        /**
         * Logout
         */
        int rc = restClientAccessor.logout(integrationTestSetupBean.getHostPath()+LOGOUT_PREFIX_PATH, restClientAccessObject);
        assertEquals(204, rc);

    }

    @Test(expected = RestClientAccessorException.class)
    public void test07_attemptAccessBulletin() {
        LOGGER.info("Running: test07_attemptAccessBulletin...");
        assertNotNull(restClientAccessor);
        //NotAuthenticatedException
        /**
         * Set up a FooBar Access Object.
         */
        Map<String, Object> accessProperties = new HashMap<>();
        accessProperties.put("access_token","BADACCESSTOKEN");
        accessProperties.put("refresh_token","BADREFRESHTOKEN");
        accessProperties.put("token_type","Bearer");
        accessProperties.put("expires_in","3600");
        RestClientAccessObject restClientAccessObject = new RestClientAccessObject(accessProperties);
        assertNotNull(restClientAccessObject);
        /**
         * Attempt and Bad Request to obtain a protected Resource.
         */
            byte[] results = (byte[]) restClientAccessor.get(
                    integrationTestSetupBean.getHostPath()
                            + BULLETIN_ENPOINT, restClientAccessObject);
            fail("Should have failed with a 'Bad Request', " +
                    "as we present an Invalid Access Token, which should have been rejected," +
                    " to access a Protected Resource, Very Bad!");
    }

    @Test
    public void test08_accessPulse() {
        LOGGER.info("Running: test08_accessPulse...");
        assertNotNull(restClientAccessor);
        /**
         * Authentication and Obtain Access Token.
         */
        RestClientAccessObject restClientAccessObject =
                restClientAccessor.getAccessToken(integrationTestSetupBean.getHostPath(), USER_EMAIL, CREDENTIALS);
        assertNotNull(restClientAccessObject);
        /**
         * Get Resource.
         */
        try {
            byte[] results = (byte[]) restClientAccessor.get(
                    integrationTestSetupBean.getHostPath()
                            + PULSE_ENDPOINT, restClientAccessObject);
            YourPulse yourPulse
                    = objectMapper.readValue(results, YourPulse.class);
            assertNotNull(yourPulse);
            /**
             * Iterate over Properties
             */
            LOGGER.info("{}", yourPulse);
        } catch (IOException ioe) {
            LOGGER.error("IOException Encountered: {}", ioe.getMessage(), ioe);
        }
        /**
         * Logout
         */
        int rc = restClientAccessor.logout(integrationTestSetupBean.getHostPath()+LOGOUT_PREFIX_PATH, restClientAccessObject);
        assertEquals(204, rc);

    }

    @Test
    public void test09_RestClientGetTest() {
        LOGGER.info("Running: test09_RestClientGetTest...");
        assertNotNull(restClientAccessor);
        /**
         * Authentication and Obtain Access Token.
         */
        RestClientAccessObject restClientAccessObject =
                restClientAccessor.getAccessToken(integrationTestSetupBean.getHostPath(), USER_EMAIL, CREDENTIALS);
        assertNotNull(restClientAccessObject);
        /**
         * Get Resource.
         */
        try {
            byte[] results = (byte[]) restClientAccessor.get(
                    integrationTestSetupBean.getHostPath()
                            +TEST_ENDPOINT, restClientAccessObject);
            Map<String,String> response
                    = objectMapper.readValue(results, Map.class);
            assertNotNull(response);
            assertEquals("OK", response.get("GET"));
            LOGGER.info("{}", response);
        } catch (IOException ioe) {
            LOGGER.error("IOException Encountered: {}", ioe.getMessage(), ioe);
        }
        /**
         * Logout
         */
        int rc = restClientAccessor.logout(integrationTestSetupBean.getHostPath()+LOGOUT_PREFIX_PATH, restClientAccessObject);
        assertEquals(204, rc);
    }

    @Test
    public void test10_RestClientPostTest() {
        LOGGER.info("Running: test10_RestClientPostTest...");
        assertNotNull(restClientAccessor);
        /**
         * Authentication and Obtain Access Token.
         */
        RestClientAccessObject restClientAccessObject =
                restClientAccessor.getAccessToken(integrationTestSetupBean.getHostPath(), USER_EMAIL, CREDENTIALS);
        assertNotNull(restClientAccessObject);
        /**
         * Get Resource.
         */
        try {
            byte[] results = (byte[]) restClientAccessor.post(
                    integrationTestSetupBean.getHostPath()
                            +TEST_ENDPOINT, restClientAccessObject);
            Map<String,Object> response
                    = objectMapper.readValue(results, Map.class);
            assertNotNull(response);
            assertEquals("OK", response.get("POST"));
            LOGGER.info("{}", response);
        } catch (IOException ioe) {
            LOGGER.error("IOException Encountered: {}", ioe.getMessage(), ioe);
        }
        /**
         * Logout
         */
        int rc = restClientAccessor.logout(integrationTestSetupBean.getHostPath()+LOGOUT_PREFIX_PATH, restClientAccessObject);
        assertEquals(204, rc);
    }

    @Test
    public void test11_RestClientPutTest() {
        LOGGER.info("Running: test11_RestClientPutTest...");
        assertNotNull(restClientAccessor);
        /**
         * Authentication and Obtain Access Token.
         */
        RestClientAccessObject restClientAccessObject =
                restClientAccessor.getAccessToken(integrationTestSetupBean.getHostPath(), USER_EMAIL, CREDENTIALS);
        assertNotNull(restClientAccessObject);
        /**
         * Get Resource.
         */
        try {
            byte[] results = (byte[]) restClientAccessor.put(
                    integrationTestSetupBean.getHostPath()
                            + TEST_ENDPOINT, restClientAccessObject);
            Map<String,Object> response
                    = objectMapper.readValue(results, Map.class);
            assertNotNull(response);
            assertEquals("OK", response.get("PUT"));
            LOGGER.info("{}", response);
        } catch (IOException ioe) {
            LOGGER.error("IOException Encountered: {}", ioe.getMessage(), ioe);
        }
        /**
         * Logout
         */
        int rc = restClientAccessor.logout(integrationTestSetupBean.getHostPath()+LOGOUT_PREFIX_PATH, restClientAccessObject);
        assertEquals(204, rc);
    }

    @Test
    public void test12_RestClientDeleteTest() {
        LOGGER.info("Running: test12_RestClientDeleteTest...");
        assertNotNull(restClientAccessor);
        /**
         * Authentication and Obtain Access Token.
         */
        RestClientAccessObject restClientAccessObject =
                restClientAccessor.getAccessToken(integrationTestSetupBean.getHostPath(), USER_EMAIL, CREDENTIALS);
        assertNotNull(restClientAccessObject);
        /**
         * Get Resource.
         */
        try {
            byte[] results = (byte[]) restClientAccessor.delete(
                    integrationTestSetupBean.getHostPath()
                            +TEST_ENDPOINT, restClientAccessObject);
            Map<String,Object> response
                    = objectMapper.readValue(results, Map.class);
            assertNotNull(response);
            assertEquals("OK", response.get("DELETE"));
            LOGGER.info("{}", response);
        } catch (IOException ioe) {
            LOGGER.error("IOException Encountered: {}", ioe.getMessage(), ioe);
        }
        /**
         * Logout
         */
        int rc = restClientAccessor.logout(integrationTestSetupBean.getHostPath()+LOGOUT_PREFIX_PATH, restClientAccessObject);
        assertEquals(204, rc);
    }




    @Test
    public void test00_first() {
        LOGGER.info("Running: test00_first --> Should be First Test Run in Integration Test Suite");
    }

    @Test
    public void test99_last() {
        LOGGER.info("Running: test99_last --> Should be Last Test Run in Integration Test Suite");
    }

}
