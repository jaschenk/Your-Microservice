package your.microservice.example.controllers.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import your.microservice.core.rest.RestClientAccessObject;
import your.microservice.core.rest.RestClientAccessor;
import your.microservice.example.Application;
import your.microservice.example.IntegrationTestSetupBean;

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


import static org.junit.Assert.*;

/**
 * ExampleClientIntegrationIT
 *
 * Integration Test Suite
 *
 * @author jeff.a.schenk@gmail.com on 03/18/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebIntegrationTest({"server.port:0"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExampleClientIntegrationIT {
    /**
     * Common Logger
     */
    protected final static org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(ExampleClientIntegrationIT.class);
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
     * Integration Helper Setup Bean
     */
    @Autowired
    private IntegrationTestSetupBean integrationTestSetupBean;


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

    /**
     * ObjectMapper
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * RESTful Client Accessor Service.
     */
    @Autowired
    private RestClientAccessor restClientAccessor;

    /**
     * Helper to obtain Established RESTful Resource Path for an Instance.
     * @return String containing Path.
     */
    private static String getLWCFAppInfo() {
        return "/api/example/v1";
    }


    /**
     * Validate Environments
     */
    @Test
    public void test01_EnvironmentSetup() {
        LOGGER.info("Running: test01_EnvironmentSetup");
        assertNotNull(integrationTestSetupBean.getHostPath());
        LOGGER.info("Host Port: {}",integrationTestSetupBean.getHostPath());

        assertNotNull(environment);

    }

    @Test
    public void test02_service_available() {
        LOGGER.info("Running: test02_service_available");
        assertNotNull(restClientAccessor);
    }

    @Test
    public void test04_getAppInfo() {
        LOGGER.info("Running: test04_getAppInfo");
        assertNotNull(restClientAccessor);

        /**
         * Get Resource.
         */
        try {
            byte[] results = (byte[]) restClientAccessor.get(
                    integrationTestSetupBean.getHostPath() + getLWCFAppInfo(), null);
            Object data  = objectMapper.readValue(results, Object.class);
            assertNotNull(data);
            LOGGER.info("AppInfo response: '{}'",data.toString());
        } catch (Exception ioe) {
            LOGGER.error("IOException Encountered: {}", ioe.getMessage(), ioe);
        }
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
