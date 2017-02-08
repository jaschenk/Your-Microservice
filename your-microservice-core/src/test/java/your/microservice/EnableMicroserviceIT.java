package your.microservice;

import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
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
import your.microservice.testutil.IntegrationTestSetupBean;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * EnableMicroserviceIT
 *
 * @author jeff.a.schenk@gmail.com on 2/21/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {MicroserviceTestApplication.class})
@WebIntegrationTest({"server.port:0", "test.environment.property:true"})
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
     * Test Integration Helper Setup Bean
     */
    @Autowired
    private IntegrationTestSetupBean integrationTestSetupBean;

    /**
     * Test Constants
     */
    private static final String SERVICE_NAME = "testmicroservice";
    private static final String APIINFO_ENDPOINT = "/api/" + SERVICE_NAME + "/v1";

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
    public void test02_getAppInfo() {
        LOGGER.info("Running: test02_getAppInfo...");
        /**
         * Access Open End Point to Obtain API Information.
         *
         * AppInfo: {"buildVersion":"1.0.0.1-SNAPSHOT","buildTimestamp":"2017-01-26 17:40:24Z",
         *           "buildName":"your-microservice-core","buildDescription":"Your Microservice Core Enable",
         *           "buildArtifactId":"your-microservice-core","buildGroupId":"your-microservice"}

         */
        given().
                header("Accept-Encoding", "application/json").
                when().
                get(integrationTestSetupBean.getHostPath()
                        + APIINFO_ENDPOINT).
                then().
                assertThat().statusCode(HttpStatus.SC_OK).
                assertThat().contentType(IntegrationTestSetupBean.APPLICATION_JSON_WITH_UTF8_ENCODING_VALUE).
                assertThat().body(containsString("\"buildVersion\":\"")).
                assertThat().body(containsString("\"buildTimestamp\":\"")).
                assertThat().body(containsString("\"buildDescription\":\"")).
                assertThat().body(containsString("\"buildVersion\":\"")).
                assertThat().body(containsString("\"buildName\":\"")).
                log().all().
                extract().asString();
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
