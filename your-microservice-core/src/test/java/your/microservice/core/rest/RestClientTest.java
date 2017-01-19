package your.microservice.core.rest;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import your.microservice.core.rest.exceptions.NotAuthenticatedException;
import your.microservice.core.rest.exceptions.RestClientAccessorException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RestClientAppConfig.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RestClientTest {
	/**
     * Common Logger
     */
    protected final static org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(RestClientTest.class);
	/**
	 * Constants
	 */
	private String principal = "joe.user@mail.com";
	private String credentials = "password";
	private String idPURL = "http://localhost:8080/api/auth";
	
	private String protectedResourceURL = "http://localhost:8080/api/info/now";
	
	/**
     * ObjectMapper
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();
	
    /**
     * Rest IdP Client Service
     */
	@Autowired
	private RestClientAccessor restClientAccessor;
	
	@Test
    public void test00_Autowired() {
		assertNotNull(restClientAccessor);
    }

    @Ignore
	@Test
    public void test01_IdPAccess() {
		assertNotNull(restClientAccessor);
		RestClientAccessObject restClientAccessObject =
				restClientAccessor.getAccessToken(idPURL, principal, credentials);
		assertNotNull(restClientAccessObject);
		assertNotNull(restClientAccessObject.getAccessToken());
		assertTrue(restClientAccessObject.getExpires()==3600);
    }
	
	@Ignore
	@Test(expected=NotAuthenticatedException.class)
    public void test02_IdPAccessFailure() {
		assertNotNull(restClientAccessor);
				restClientAccessor.getAccessToken(idPURL, principal, "BAD_CREDENTIALS");
		fail("Should have thrown a NotAuthenticated Exception, but did not, very bad!");
    }
	
	@Ignore
	@Test(expected=RestClientAccessorException.class)
    public void test03_IdPAccessAttempt() {
		assertNotNull(restClientAccessor);
		/**
		 * Hack an Access Object
		 */
		Map<String, Object> accessProperties = new HashMap<>();
		accessProperties.put("token", UUID.randomUUID().toString()); // Bogus Token...
		RestClientAccessObject restClientAccessObject = new RestClientAccessObject(accessProperties);
				restClientAccessor.get(protectedResourceURL, restClientAccessObject);
		fail("Should have thrown a RestClientAccessor Exception, but did not, very bad!");
    }
	
	@Ignore
	@Test
    public void test04_IdPAccessProtectedResource() {
		assertNotNull(restClientAccessor);
		RestClientAccessObject restClientAccessObject =
				restClientAccessor.getAccessToken(idPURL, principal, credentials);
		assertNotNull(restClientAccessObject);
		assertNotNull(restClientAccessObject.getAccessToken());
		assertTrue(restClientAccessObject.getExpires()==3600);
		/**
         * Get Resource.
         */
        try {
            byte[] results = (byte[]) restClientAccessor.get(protectedResourceURL, restClientAccessObject);
            @SuppressWarnings("unchecked")
			Map<String,Object> data = objectMapper.readValue(results, Map.class);
            assertNotNull(data);
            LOGGER.info("Data:");
            for(String key : data.keySet()) {
            	LOGGER.info(" ++ '{}' = '{}'", key, data.get(key));
            }
        } catch (Exception ioe) {
            LOGGER.error("IOException Encountered: {}", ioe.getMessage(), ioe);
        }
		
		
    }
	
}
