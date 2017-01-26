package your.microservice.core.integration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import your.microservice.MicroserviceTestApplication;
import your.microservice.core.dm.dto.system.YourPulse;
import your.microservice.core.rest.RestIdPClientAccessObject;
import your.microservice.core.rest.RestIdPClientAccessor;
import your.microservice.core.rest.exceptions.NotAuthenticatedException;
import your.microservice.core.rest.exceptions.RestClientAccessorException;
import your.microservice.core.system.messaging.model.YourMSBulletinBroadcastNotification;
import your.microservice.testutil.IntegrationTestSetupBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * IdPAccessIT
 *
 * @author jeff.a.schenk@gmail.com on 2/21/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {MicroserviceTestApplication.class})
@WebIntegrationTest({"server.port:0", "test.environment.property:true"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IdPAccessIT {
    /**
     * Common Logger
     */
    protected final static org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(IdPAccessIT.class);

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
     * Test Integration Helper Setup Bean
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
    private RestIdPClientAccessor restIdPClientAccessor;

    /**
     * Test Constants
     */
    private static final String USER_EMAIL = "joe.user@mail.com";
    private static final String ADMIN_EMAIL = "admin.entity@mail.com";
    private static final String CREDENTIALS = "TestPassword";
    private static final String SERVICE_NAME = "testmicroservice";

    private static final String BULLETIN_ENPOINT = "/api/" + SERVICE_NAME + "/v1/system/bulletin";
    private static final String PULSE_ENDPOINT = "/api/" + SERVICE_NAME + "/v1/system/pulse";
    private static final String TEST_ENDPOINT = "/api/" + SERVICE_NAME + "/v1/test";

    /**
     * Injected Values
     */
    @Value("${your.microservice.security.test.user.resource}")
    private String userRoute;

    @Value("${your.microservice.security.test.protected.resource}")
    private String protectedRoute;

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

        assertEquals("true",
                environment.getProperty("test.environment.property"));
    }

    @Test
    public void test03_IdPAccess() {
        LOGGER.info("Running: test03_IdPAccess...");
        assertNotNull(restIdPClientAccessor);
        RestIdPClientAccessObject restIdPClientAccessObject =
                restIdPClientAccessor.getAccessToken(
                        integrationTestSetupBean.getHostPath() + RestIdPClientAccessor.YOUR_MICROSERVICE_IDP_TOKEN_REQUEST_RESOURCE_PATH,
                        USER_EMAIL, CREDENTIALS);
        assertNotNull(restIdPClientAccessObject);
        assertNotNull(restIdPClientAccessObject.getAccessToken());
        assertTrue(restIdPClientAccessObject.getExpires() >= 3600);
    }

    @Test(expected = NotAuthenticatedException.class)
    public void test04_IdPAccessFailure() {
        LOGGER.info("Running: test04_IdPAccessFailure...");
        assertNotNull(restIdPClientAccessor);
        restIdPClientAccessor.getAccessToken(
                integrationTestSetupBean.getHostPath() + RestIdPClientAccessor.YOUR_MICROSERVICE_IDP_TOKEN_REQUEST_RESOURCE_PATH,
                USER_EMAIL, "BAD_CREDENTIALS");
        fail("Should have thrown a NotAuthenticated Exception, but did not, very bad!");
    }

    @Test(expected = RestClientAccessorException.class)
    public void test05_IdPAccessAttempt() {
        LOGGER.info("Running: test05_IdPAccessAttempt...");
        assertNotNull(restIdPClientAccessor);
        /**
         * Hack an Access Object
         */
        Map<String, Object> accessProperties = new HashMap<>();
        accessProperties.put("token", UUID.randomUUID().toString()); // Bogus Token...
        RestIdPClientAccessObject RestIdPClientAccessObject = new RestIdPClientAccessObject(accessProperties);
        restIdPClientAccessor.get(integrationTestSetupBean.getHostPath()+TEST_ENDPOINT, RestIdPClientAccessObject);
        fail("Should have thrown a RestClientAccessor Exception, but did not, very bad!");
    }

    @Test
    public void test06_IdPAccessProtectedResource() {
        LOGGER.info("Running: test06_IdPAccessProtectedResource...");
        assertNotNull(restIdPClientAccessor);
        RestIdPClientAccessObject restIdPClientAccessObject =
                restIdPClientAccessor.getAccessToken(
                        integrationTestSetupBean.getHostPath() + RestIdPClientAccessor.YOUR_MICROSERVICE_IDP_TOKEN_REQUEST_RESOURCE_PATH,
                        USER_EMAIL, CREDENTIALS);
        assertNotNull(restIdPClientAccessObject);
        assertNotNull(restIdPClientAccessObject.getAccessToken());
        assertTrue(restIdPClientAccessObject.getExpires() >= 3600);
        /**
         * Get Resource.
         */
            byte[] results = (byte[]) restIdPClientAccessor.get(integrationTestSetupBean.getHostPath() + TEST_ENDPOINT, restIdPClientAccessObject);
            assertNotNull(results);
            assertEquals("OK", new String(results));
            LOGGER.info("{}", new String(results));
    }

    @Test
    public void test07_getAccessToken() {
        LOGGER.info("Running: test07_getAccessToken...");
        assertNotNull(restIdPClientAccessor);
        RestIdPClientAccessObject restIdPClientAccessObject =
                restIdPClientAccessor.getAccessToken(
                        integrationTestSetupBean.getHostPath() + RestIdPClientAccessor.YOUR_MICROSERVICE_IDP_TOKEN_REQUEST_RESOURCE_PATH,
                        USER_EMAIL, CREDENTIALS);
        assertNotNull(restIdPClientAccessObject);

        LOGGER.debug(" Access Token: [{}]", restIdPClientAccessObject.getAccessToken());
        LOGGER.debug("   Token Type: [{}]", restIdPClientAccessObject.getTokenType());
        LOGGER.debug("Token Expires: [{}]", restIdPClientAccessObject.getExpires());

        int rc =
                restIdPClientAccessor.logout(integrationTestSetupBean.getHostPath(),
                        restIdPClientAccessObject);
        assertEquals(200, rc);

    }

    @Test(expected = NotAuthenticatedException.class)
    public void test08_getAccessToken() {
        LOGGER.info("Running: test08_getAccessToken...");
        assertNotNull(restIdPClientAccessor);
        restIdPClientAccessor.getAccessToken(integrationTestSetupBean.getHostPath() + RestIdPClientAccessor.YOUR_MICROSERVICE_IDP_TOKEN_REQUEST_RESOURCE_PATH,
                USER_EMAIL, "FOOBAR");

        fail("Should have failed with a 'Not Authenticated Exception', " +
                "as we present an Invalid Credentials, which should have been rejected," +
                " to access a Protected Resource, Very Bad!");
    }

    @Test
    public void test09_accessBulletin() {
        LOGGER.info("Running: test09_accessBulletin...");
        assertNotNull(restIdPClientAccessor);
        /**
         * Authentication and Obtain Access Token.
         */
        RestIdPClientAccessObject RestIdPClientAccessObject =
                restIdPClientAccessor.getAccessToken(integrationTestSetupBean.getHostPath() + RestIdPClientAccessor.YOUR_MICROSERVICE_IDP_TOKEN_REQUEST_RESOURCE_PATH, USER_EMAIL, CREDENTIALS);
        assertNotNull(RestIdPClientAccessObject);
        /**
         * Get Resource.
         */
        try {
            byte[] results = (byte[]) restIdPClientAccessor.get(
                    integrationTestSetupBean.getHostPath()
                            + BULLETIN_ENPOINT, RestIdPClientAccessObject);
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
        int rc = restIdPClientAccessor.logout(integrationTestSetupBean.getHostPath(), RestIdPClientAccessObject);
        assertEquals(200, rc);

    }

    @Test(expected = RestClientAccessorException.class)
    public void test10_attemptAccessBulletin() {
        LOGGER.info("Running: test10_attemptAccessBulletin...");
        assertNotNull(restIdPClientAccessor);
        //NotAuthenticatedException
        /**
         * Set up a FooBar Access Object.
         */
        Map<String, Object> accessProperties = new HashMap<>();
        accessProperties.put("access_token", "BADACCESSTOKEN");
        accessProperties.put("refresh_token", "BADREFRESHTOKEN");
        accessProperties.put("token_type", "Bearer");
        accessProperties.put("expires_in", "3600");
        RestIdPClientAccessObject RestIdPClientAccessObject = new RestIdPClientAccessObject(accessProperties);
        assertNotNull(RestIdPClientAccessObject);
        /**
         * Attempt and Bad Request to obtain a protected Resource.
         */
        byte[] results = (byte[]) restIdPClientAccessor.get(
                integrationTestSetupBean.getHostPath()
                        + BULLETIN_ENPOINT, RestIdPClientAccessObject);
        fail("Should have failed with a 'Bad Request', " +
                "as we present an Invalid Access Token, which should have been rejected," +
                " to access a Protected Resource, Very Bad!");
    }

    @Test
    public void test11_accessPulse() {
        LOGGER.info("Running: test11_accessPulse...");
        assertNotNull(restIdPClientAccessor);
        /**
         * Authentication and Obtain Access Token.
         */
        RestIdPClientAccessObject RestIdPClientAccessObject =
                restIdPClientAccessor.getAccessToken(integrationTestSetupBean.getHostPath() + RestIdPClientAccessor.YOUR_MICROSERVICE_IDP_TOKEN_REQUEST_RESOURCE_PATH, USER_EMAIL, CREDENTIALS);
        assertNotNull(RestIdPClientAccessObject);
        /**
         * Get Resource.
         */
        try {
            byte[] results = (byte[]) restIdPClientAccessor.get(
                    integrationTestSetupBean.getHostPath()
                            + PULSE_ENDPOINT, RestIdPClientAccessObject);
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
        int rc = restIdPClientAccessor.logout(integrationTestSetupBean.getHostPath(), RestIdPClientAccessObject);
        assertEquals(200, rc);

    }

    @Test
    public void test12_RestClientGetTest() {
        LOGGER.info("Running: test12_RestClientGetTest...");
        assertNotNull(restIdPClientAccessor);
        /**
         * Authentication and Obtain Access Token.
         */
        RestIdPClientAccessObject RestIdPClientAccessObject =
                restIdPClientAccessor.getAccessToken(integrationTestSetupBean.getHostPath() + RestIdPClientAccessor.YOUR_MICROSERVICE_IDP_TOKEN_REQUEST_RESOURCE_PATH, USER_EMAIL, CREDENTIALS);
        assertNotNull(RestIdPClientAccessObject);
        /**
         * Get Resource.
         */
            byte[] results = (byte[]) restIdPClientAccessor.get(
                    integrationTestSetupBean.getHostPath()
                            + TEST_ENDPOINT, RestIdPClientAccessObject);

            assertNotNull(results);
            assertEquals("OK", new String(results));
            LOGGER.info("{}", new String(results));
        /**
         * Logout
         */
        int rc = restIdPClientAccessor.logout(integrationTestSetupBean.getHostPath(), RestIdPClientAccessObject);
        assertEquals(200, rc);
    }

    @Test
    public void test13_RestClientPostTest() {
        LOGGER.info("Running: test13_RestClientPostTest...");
        assertNotNull(restIdPClientAccessor);
        /**
         * Authentication and Obtain Access Token.
         */
        RestIdPClientAccessObject RestIdPClientAccessObject =
                restIdPClientAccessor.getAccessToken(integrationTestSetupBean.getHostPath() + RestIdPClientAccessor.YOUR_MICROSERVICE_IDP_TOKEN_REQUEST_RESOURCE_PATH, USER_EMAIL, CREDENTIALS);
        assertNotNull(RestIdPClientAccessObject);
        /**
         * Post Resource.
         */
            byte[] results = (byte[]) restIdPClientAccessor.post(
                    integrationTestSetupBean.getHostPath()
                            + TEST_ENDPOINT, RestIdPClientAccessObject);
            assertNotNull(results);
            assertEquals("OK", new String(results));
            LOGGER.info("{}", new String(results));
        /**
         * Logout
         */
        int rc = restIdPClientAccessor.logout(integrationTestSetupBean.getHostPath(), RestIdPClientAccessObject);
        assertEquals(200, rc);
    }

    @Test
    public void test14_RestClientPutTest() {
        LOGGER.info("Running: test13_RestClientPutTest...");
        assertNotNull(restIdPClientAccessor);
        /**
         * Authentication and Obtain Access Token.
         */
        RestIdPClientAccessObject RestIdPClientAccessObject =
                restIdPClientAccessor.getAccessToken(integrationTestSetupBean.getHostPath() + RestIdPClientAccessor.YOUR_MICROSERVICE_IDP_TOKEN_REQUEST_RESOURCE_PATH, USER_EMAIL, CREDENTIALS);
        assertNotNull(RestIdPClientAccessObject);
        /**
         * Put Resource.
         */
            byte[] results = (byte[]) restIdPClientAccessor.put(
                    integrationTestSetupBean.getHostPath()
                            + TEST_ENDPOINT, RestIdPClientAccessObject);
            assertNotNull(results);
            assertEquals("OK", new String(results));
            LOGGER.info("{}", new String(results));
        /**
         * Logout
         */
        int rc = restIdPClientAccessor.logout(integrationTestSetupBean.getHostPath(), RestIdPClientAccessObject);
        assertEquals(200, rc);
    }

    @Test
    public void test15_RestClientDeleteTest() {
        LOGGER.info("Running: test14_RestClientDeleteTest...");
        assertNotNull(restIdPClientAccessor);
        /**
         * Authentication and Obtain Access Token.
         */
        RestIdPClientAccessObject RestIdPClientAccessObject =
                restIdPClientAccessor.getAccessToken(integrationTestSetupBean.getHostPath() + RestIdPClientAccessor.YOUR_MICROSERVICE_IDP_TOKEN_REQUEST_RESOURCE_PATH, USER_EMAIL, CREDENTIALS);
        assertNotNull(RestIdPClientAccessObject);
        /**
         * Delete Resource.
         */
            byte[] results = (byte[]) restIdPClientAccessor.delete(
                    integrationTestSetupBean.getHostPath()
                            + TEST_ENDPOINT, RestIdPClientAccessObject);
            assertNotNull(results);
            assertEquals("OK", new String(results));
            LOGGER.info("{}", new String(results));
        /**
         * Logout
         */
        int rc = restIdPClientAccessor.logout(integrationTestSetupBean.getHostPath(), RestIdPClientAccessObject);
        assertEquals(200, rc);
    }

    @Test
    public void test16_AccessProtectedMethodResource() {
        LOGGER.info("Running: test16_AccessAProtectedMethodResource...");
        assertNotNull(restIdPClientAccessor);
        RestIdPClientAccessObject restIdPClientAccessObject =
                restIdPClientAccessor.getAccessToken(
                        integrationTestSetupBean.getHostPath() + RestIdPClientAccessor.YOUR_MICROSERVICE_IDP_TOKEN_REQUEST_RESOURCE_PATH,
                        USER_EMAIL, CREDENTIALS);
        assertNotNull(restIdPClientAccessObject);

        LOGGER.debug(" Access Token: [{}]", restIdPClientAccessObject.getAccessToken());
        LOGGER.debug("   Token Type: [{}]", restIdPClientAccessObject.getTokenType());
        LOGGER.debug("Token Expires: [{}]", restIdPClientAccessObject.getExpires());

        /**
         * Access the Protected Method Resource
         */
        try {
            restIdPClientAccessor.get(integrationTestSetupBean.getAbsolutePath(protectedRoute), restIdPClientAccessObject);
        } catch (RestClientAccessorException rcae) {
            if (rcae.getResponseCode() != 403 && !rcae.getMessage().equalsIgnoreCase("Forbidden")) {
                fail("Method Resource should have been Forbidden!");
            }
        }
        /**
         * Logout
         */
        int rc =
                restIdPClientAccessor.logout(integrationTestSetupBean.getHostPath(),
                        restIdPClientAccessObject);
        assertEquals(200, rc);
    }

    @Test
    public void test17_AccessProtectedMethodResource() {
        LOGGER.info("Running: test17_AccessAProtectedMethodResource...");
        assertNotNull(restIdPClientAccessor);
        RestIdPClientAccessObject restIdPClientAccessObject =
                restIdPClientAccessor.getAccessToken(
                        integrationTestSetupBean.getHostPath() + RestIdPClientAccessor.YOUR_MICROSERVICE_IDP_TOKEN_REQUEST_RESOURCE_PATH,
                        ADMIN_EMAIL, CREDENTIALS);
        assertNotNull(restIdPClientAccessObject);

        LOGGER.debug(" Access Token: [{}]", restIdPClientAccessObject.getAccessToken());
        LOGGER.debug("   Token Type: [{}]", restIdPClientAccessObject.getTokenType());
        LOGGER.debug("Token Expires: [{}]", restIdPClientAccessObject.getExpires());

        /**
         * Access the Protected Method Resource
         */
        try {
            restIdPClientAccessor.get(integrationTestSetupBean.getAbsolutePath(protectedRoute), restIdPClientAccessObject);
        } catch (RestClientAccessorException rcae) {
                fail("Method Resource should Not have been Forbidden!");
        }
        /**
         * Logout
         */
        int rc =
                restIdPClientAccessor.logout(integrationTestSetupBean.getHostPath(),
                        restIdPClientAccessObject);
        assertEquals(200, rc);
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
