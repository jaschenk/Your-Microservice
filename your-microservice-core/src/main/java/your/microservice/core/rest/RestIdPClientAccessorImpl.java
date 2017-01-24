package your.microservice.core.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import your.microservice.core.rest.exceptions.NotAuthenticatedException;
import your.microservice.core.rest.exceptions.RestClientAccessorException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * RestIdPClientAccessorImpl
 *
 * Provides RESTful Client Accessor Implementation.
 *
 * Implements necessary Token Authentication
 * to Interface with an existing Protected Resource.
 *
 * @author jeff.a.schenk@gmail.com
 */
@Service
public class RestIdPClientAccessorImpl implements RestIdPClientAccessor {
    /**
     * Common Logger
     */
    protected final static org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(RestIdPClientAccessor.class);

    /**
     * Static Object Mapper
     */
    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * initialization
     * Entered when Bean is initialized.
     */
    @PostConstruct
    public void initialization() {
        LOGGER.info("Initialization of Your Microservice RESTful Client Service Implementation has been wired into runtime Environment.");
    }

    /**
     * destroyBean
     * Entered when Bean is being destroyed or torn down from the runtime Environment.
     * Simple indicate destruction...
     */
    @PreDestroy
    public void destroyBean() {
        LOGGER.info("Your Microservice RESTful Client Service Implementation has been removed from the runtime Environment.");
    }

    /**
     * getAccessToken
     *
     * Provides initial Method to gain Access to a Protected Resource, which is
     * to obtain an Access Token for the Protected Resource.
     *
     * If the url protocol is not HTTPS, it will allow, but produce a warning.
     *
     * @param url         Enterprise Eco-System
     * @param principal   User Email Address or UserId to be validated against a IdP Account Store.
     * @param credentials User Credentials or Password to be validated against a IdP Account Store.
     * @return RestIdPClientAccessObject which contains the Access Token, Token Type and
     * Token Expiration in Milliseconds.
     */
    @SuppressWarnings("unchecked")
	@Override
    public RestIdPClientAccessObject getAccessToken(String url, String principal, String credentials) {
        /**
         * Provide Debugging if requested.
         */
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Performing getAccessToken: URL:[{}] Principal:[{}]", url, principal);
        }
        /**
         * Establish Default Http Client and Applicable Headers.
         */
        CloseableHttpClient httpClient = HttpClients.createDefault();
        
        //HttpPost httpPost = new HttpPost(url + OAUTH_TOKEN_REQUEST_RESOURCE_PATH);
        
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(ORIGIN_HEADER_NAME, url);
        httpPost.addHeader(CONTENT_TYPE_HEADER_NAME, ACCEPT_HEADER_JSON_HEADER_DEFAULT_VALUE);
        /**
         * Establish our Request JSON Payload.
         */
        Map<String,String> payload = new HashMap<>();
        payload.put(USERNAME_PNAME,principal);
        payload.put(PASSWORD_PNAME, credentials);
        try {
            httpPost.addHeader(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_JSON);
            StringEntity input = new StringEntity(objectMapper.writeValueAsString(payload), UTF8);
            httpPost.setEntity(input);
        } catch (JsonProcessingException jpe) {
            LOGGER.error("Error Performing 'POST' while preparing Payload, JSON Exception:[{}].",
                    jpe.getMessage(), jpe);
            throw new RestClientAccessorException(jpe.getMessage(), -1, jpe);
        } 
        /**
         * Perform POST to Obtain Access Token
         */
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            /**
             * Determine if this Authentication was Successful or Not...
             */
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                RestIdPClientAccessObject RestIdPClientAccessObject =
                        new RestIdPClientAccessObject(objectMapper.readValue(httpResponse.getEntity().getContent(), HashMap.class));
                return RestIdPClientAccessObject;
            } else if (httpResponse.getStatusLine().getStatusCode() >= 400 &&
                    httpResponse.getStatusLine().getStatusCode() <= 499) {
                /**
                 * Log and reflect an Not Authenticated.
                 */
                LOGGER.warn("Not Authenticated Exception raised for Principal:'{}' Response Code:'{}'.",
                        httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().getReasonPhrase());
                throw new NotAuthenticatedException(principal,
                        httpResponse.getStatusLine().getReasonPhrase(),
                        httpResponse.getStatusLine().getStatusCode());
            } else {
                LOGGER.warn("Runtime Exception Condition raised for Principal:'{}' Response Code:'{}'.",
                        httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().getReasonPhrase());
                throw new RestClientAccessorException(principal,
                        httpResponse.getStatusLine().getReasonPhrase(),
                        httpResponse.getStatusLine().getStatusCode());
            }
        } catch (IOException ioe) {
            LOGGER.error("Unable to Obtain Access Token, IO Exception:'{}', returning Null.", ioe.getMessage(), ioe);
            throw new RestClientAccessorException(principal, ioe.getMessage(), -1, ioe);
        } finally {
            try {
                /**
                 * Close HTTP Client.
                 */
                httpClient.close();
            } catch (IOException ioe) {
                LOGGER.error("IO Exception Closing HTTP Client Connection:[{}].",
                        ioe.getMessage(), ioe);
            }
        }
    }

    /**
     * getAccessToken
     *
     * Provides subsequent Method to gain Access to a Protected Resource, which is
     * to use a Refresh Token to obtain a new Access Token for the Protected Resource.
     *
     * @param url                    Enterprise Eco-System
     * @param RestIdPClientAccessObject Access Object, which contains Refresh Token.
     * @return RestIdPClientAccessObject which contains the new Access Token, Refresh Token, Token Type and
     * Token Expiration in Milliseconds.
     */
    @SuppressWarnings("unchecked")
	@Override
    public RestIdPClientAccessObject getAccessToken(String url, RestIdPClientAccessObject RestIdPClientAccessObject) {
        // TODO :: Fix this.....
    	
    	/**
         * Provide Debugging if requested.
         */
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Performing getAccessToken: URL:[{}] via Refresh Token.", url);
        }
        /**
         * Establish Default Http Client and Applicable Headers.
         */
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //HttpPost httpPost = new HttpPost(url + OAUTH_TOKEN_REQUEST_RESOURCE_PATH);
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(ORIGIN_HEADER_NAME, url);
        httpPost.addHeader(CONTENT_TYPE_HEADER_NAME, ACCEPT_HEADER_JSON_HEADER_DEFAULT_VALUE);
        
        
        
        /**
         * Perform POST to Obtain Access Token
         */
        try {
            //HttpEntity postParams = new UrlEncodedFormEntity(urlParameters);
            //httpPost.setEntity(postParams);
        	
        	
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            /**
             * Determine if this Authentication was Successful or Not...
             */
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                RestIdPClientAccessObject =
                        new RestIdPClientAccessObject(objectMapper.readValue(httpResponse.getEntity().getContent(), HashMap.class));
                return RestIdPClientAccessObject;
            } else if (httpResponse.getStatusLine().getStatusCode() >= 400 &&
                    httpResponse.getStatusLine().getStatusCode() <= 499) {
                /**
                 * Log and reflect an Not Authenticated.
                 */
                LOGGER.warn("Not Authenticated Exception raised for Principal:[{}] Response Code:[{}] {}.",
                        httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().getReasonPhrase());
                throw new NotAuthenticatedException(null,
                        httpResponse.getStatusLine().getReasonPhrase(),
                        httpResponse.getStatusLine().getStatusCode());
            } else {
                LOGGER.warn("Runtime Exception Condition raised for Principal:[{}] Response Code:[{}] {}.",
                        httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().getReasonPhrase());
                throw new RestClientAccessorException(null,
                        httpResponse.getStatusLine().getReasonPhrase(),
                        httpResponse.getStatusLine().getStatusCode());
            }
        } catch (IOException ioe) {
            LOGGER.error("Unable to Obtain Access Token using refresh Token, IO Exception:[{}], returning Null.", ioe.getMessage(), ioe);
            throw new RestClientAccessorException(null, ioe.getMessage(), -1, ioe);
        } finally {
            try {
                /**
                 * Close HTTP Client.
                 */
                httpClient.close();
            } catch (IOException ioe) {
                LOGGER.error("IO Exception Closing HTTP Client Connection:[{}].",
                        ioe.getMessage(), ioe);
            }
        }
    }

    /**
     * get
     *
     * Perform RESTful 'GET' Method using specified URL.
     *
     * @param url                    Enterprise Eco-System
     * @param RestIdPClientAccessObject Access Object, which contains Access Token.
     * @return Object Response
     */
    @Override
    public Object get(String url, RestIdPClientAccessObject RestIdPClientAccessObject) {
        /**
         * Provide Debugging if requested.
         */
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Performing get: URL:[{}]", url);
        }
        /**
         * Establish Default Http Client and Applicable Headers.
         */
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_BEARER_VALUE + 
        		RestIdPClientAccessObject.getAccessToken());
        httpGet.addHeader(ACCEPT_HEADER_NAME, ACCEPT_HEADER_JSON_HEADER_DEFAULT_VALUE);
        /**
         * Perform GET Method
         */
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                return toByteArray(httpResponse.getEntity().getContent());
            } else {
                LOGGER.warn("Runtime Exception Condition raised Accessing:[{}] Response Code:[{}] {}.",
                        url,
                        httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().getReasonPhrase());
                throw new RestClientAccessorException(url,
                        httpResponse.getStatusLine().getReasonPhrase(),
                        httpResponse.getStatusLine().getStatusCode());
            }
        } catch (IOException ioe) {
            LOGGER.error("Error Performing 'GET', IO Exception:[{}].", ioe.getMessage(), ioe);
            throw new RestClientAccessorException(ioe.getMessage(), -1, ioe);
        } finally {
            try {
                /**
                 * Close HTTP Client.
                 */
                httpClient.close();
            } catch (IOException ioe) {
                LOGGER.error("IO Exception Closing HTTP Client Connection:[{}].",
                        ioe.getMessage(), ioe);
            }
        }
    }

    /**
     * post
     *
     * Perform RESTful 'POST' Method using specified URL.
     *
     * @param url                    Enterprise Eco-System
     * @param RestIdPClientAccessObject Access Object, which contains Access Token.
     * @return Object Response
     */
    @Override
    public Object post(String url, RestIdPClientAccessObject RestIdPClientAccessObject) {
       return post(url, null, RestIdPClientAccessObject);
    }

    /**
     * post
     *
     * Perform RESTful 'POST' Method using specified URL.
     *
     * @param url                    Enterprise Eco-System
     * @param payload                DTO Payload to be sent with Resource.
     * @param RestIdPClientAccessObject Access Object, which contains Access Token.
     * @return Object Response
     */
    @Override
    public Object post(String url, Object payload, RestIdPClientAccessObject RestIdPClientAccessObject) {
        /**
         * Provide Debugging if requested.
         */
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Performing post: URL:[{}]", url);
        }
        /**
         * Establish Default Http Client and Applicable Headers.
         */
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_BEARER_VALUE + 
        		RestIdPClientAccessObject.getAccessToken());
        httpPost.addHeader(ACCEPT_HEADER_NAME, ACCEPT_HEADER_JSON_HEADER_DEFAULT_VALUE);
        
        /**
         * Add the Payload
         */
        if (payload != null) {
            try {
                httpPost.addHeader(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_JSON);
                StringEntity input = new StringEntity(objectMapper.writeValueAsString(payload), UTF8);
                httpPost.setEntity(input);
            } catch (JsonProcessingException jpe) {
                LOGGER.error("Error Performing 'POST' while preparing Payload, JSON Exception:[{}].",
                        jpe.getMessage(), jpe);
                throw new RestClientAccessorException(jpe.getMessage(), -1, jpe);
            }
        }
        /**
         * Perform POST Method
         */
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                return toByteArray(httpResponse.getEntity().getContent());
            } else {
                LOGGER.warn("Runtime Exception Condition raised Accessing:[{}] Response Code:[{}] {}.",
                        url,
                        httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().getReasonPhrase());
                throw new RestClientAccessorException(url,
                        httpResponse.getStatusLine().getReasonPhrase(),
                        httpResponse.getStatusLine().getStatusCode());
            }
        } catch (IOException ioe) {
            LOGGER.error("Error Performing 'POST', IO Exception:[{}].", ioe.getMessage(), ioe);
            throw new RestClientAccessorException(ioe.getMessage(), -1, ioe);
        } finally {
            try {
                /**
                 * Close HTTP Client.
                 */
                httpClient.close();
            } catch (IOException ioe) {
                LOGGER.error("IO Exception Closing HTTP Client Connection:[{}].",
                        ioe.getMessage(), ioe);
            }
        }
    }

    /**
     * put
     *
     * Perform RESTful 'PUT' Method using specified URL.
     *
     * @param url                    Enterprise Eco-System
     * @param RestIdPClientAccessObject Access Object, which contains Access Token.
     * @return Object Response
     */
    @Override
    public Object put(String url, RestIdPClientAccessObject RestIdPClientAccessObject) {
        return put(url, null, RestIdPClientAccessObject);
    }

    /**
     * put
     *
     * Perform RESTful 'PUT' Method using specified URL.
     *
     * @param url                    Enterprise Eco-System
     * @param payload                DTO Payload to be sent with Resource.
     * @param RestIdPClientAccessObject Access Object, which contains Access Token.
     * @return Object Response
     */
    @Override
    public Object put(String url, Object payload, RestIdPClientAccessObject RestIdPClientAccessObject) {
        /**
         * Provide Debugging if requested.
         */
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Performing put: URL:[{}]", url);
        }
        /**
         * Establish Default Http Client and Applicable Headers.
         */
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPut httpPut = new HttpPut(url);
        httpPut.addHeader(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_BEARER_VALUE + 
        		RestIdPClientAccessObject.getAccessToken());
        httpPut.addHeader(ACCEPT_HEADER_NAME, ACCEPT_HEADER_JSON_HEADER_DEFAULT_VALUE);
        /**
         * Add the Payload
         */
        if (payload != null) {
            try {
                httpPut.addHeader(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_JSON);
                StringEntity input = new StringEntity(objectMapper.writeValueAsString(payload), UTF8);
                httpPut.setEntity(input);
            } catch (JsonProcessingException jpe) {
                LOGGER.error("Error Performing 'PUT' while preparing Payload, JSON Exception:[{}].",
                        jpe.getMessage(), jpe);
                throw new RestClientAccessorException(jpe.getMessage(), -1, jpe);
            }
        }
        /**
         * Perform PUT Method
         */
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpPut);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                return toByteArray(httpResponse.getEntity().getContent());
            } else {
                LOGGER.warn("Runtime Exception Condition raised Accessing:[{}] Response Code:[{}] {}.",
                        url,
                        httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().getReasonPhrase());
                throw new RestClientAccessorException(url,
                        httpResponse.getStatusLine().getReasonPhrase(),
                        httpResponse.getStatusLine().getStatusCode());
            }
        } catch (IOException ioe) {
            LOGGER.error("Error Performing 'PUT', IO Exception:[{}].", ioe.getMessage(), ioe);
            throw new RestClientAccessorException(ioe.getMessage(), -1, ioe);
        } finally {
            try {
                /**
                 * Close HTTP Client.
                 */
                httpClient.close();
            } catch (IOException ioe) {
                LOGGER.error("IO Exception Closing HTTP Client Connection:[{}].",
                        ioe.getMessage(), ioe);
            }
        }
    }

    /**
     * delete
     *
     * Perform RESTful 'DELETE' Method using specified URL.
     *
     * @param url                    Enterprise Eco-System
     * @param RestIdPClientAccessObject Access Object, which contains Access Token.
     * @return Object Response
     */
    @Override
    public Object delete(String url, RestIdPClientAccessObject RestIdPClientAccessObject) {
        /**
         * Provide Debugging if requested.
         */
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Performing delete: URL:[{}]", url);
        }
        /**
         * Establish Default Http Client and Applicable Headers.
         */
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete(url);
        httpDelete.addHeader(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_BEARER_VALUE + 
        		RestIdPClientAccessObject.getAccessToken());
        httpDelete.addHeader(ACCEPT_HEADER_NAME, ACCEPT_HEADER_JSON_HEADER_DEFAULT_VALUE);
        /**
         * Perform DELETE Method
         */
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpDelete);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                return toByteArray(httpResponse.getEntity().getContent());
            } else {
                LOGGER.warn("Runtime Exception Condition raised Accessing:[{}] Response Code:[{}] {}.",
                        url,
                        httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().getReasonPhrase());
                throw new RestClientAccessorException(url,
                        httpResponse.getStatusLine().getReasonPhrase(),
                        httpResponse.getStatusLine().getStatusCode());
            }
        } catch (IOException ioe) {
            LOGGER.error("Error Performing 'DELETE', IO Exception:[{}].", ioe.getMessage(), ioe);
            throw new RestClientAccessorException(ioe.getMessage(), -1, ioe);
        } finally {
            try {
                /**
                 * Close HTTP Client.
                 */
                httpClient.close();
            } catch (IOException ioe) {
                LOGGER.error("IO Exception Closing HTTP Client Connection:[{}].",
                        ioe.getMessage(), ioe);
            }
        }
    }

    /**
     * logout
     *
     * Perform Logout to expire supplied Refresh Token.
     *
     * @param url                    Enterprise Eco-System
     * @param RestIdPClientAccessObject Access Object, which contains Access Token.
     * @return int Logout HTTPs Return Code.
     */
    @Override
    public int logout(String url, RestIdPClientAccessObject RestIdPClientAccessObject) {
        /**
         * Provide Debugging if requested.
         */
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Performing logout: URL:[{}]", url + YOUR_MICROSERVICE_IDP_TOKEN_LOGOUT_RESOURCE_PATH);
        }
        /**
         * Establish Default Http Client and Applicable Headers.
         */
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url + YOUR_MICROSERVICE_IDP_TOKEN_LOGOUT_RESOURCE_PATH);
        httpGet.addHeader(AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_BEARER_VALUE + 
        		RestIdPClientAccessObject.getAccessToken());
        httpGet.addHeader(ACCEPT_HEADER_NAME, ACCEPT_HEADER_JSON_HEADER_DEFAULT_VALUE);
        /**
         * Perform Logout...
         */
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200 ||
                    httpResponse.getStatusLine().getStatusCode() == 204 ||
                    httpResponse.getStatusLine().getStatusCode() == 302) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Token Based Logout Successful!");
                }
                return httpResponse.getStatusLine().getStatusCode();
            } else {
                LOGGER.warn("Runtime Exception Condition raised Accessing:[{}] Response Code:[{}] {}.",
                        url + YOUR_MICROSERVICE_IDP_TOKEN_LOGOUT_RESOURCE_PATH,
                        httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().getReasonPhrase());
                throw new RestClientAccessorException(url + YOUR_MICROSERVICE_IDP_TOKEN_LOGOUT_RESOURCE_PATH,
                        httpResponse.getStatusLine().getReasonPhrase(),
                        httpResponse.getStatusLine().getStatusCode());
            }
        } catch (IOException ioe) {
            LOGGER.error("Error Performing 'LOGOUT', IO Exception:[{}].", ioe.getMessage(), ioe);
            throw new RestClientAccessorException(ioe.getMessage(), -1, ioe);
        } finally {
            try {
                /**
                 * Close HTTP Client.
                 */
                httpClient.close();
            } catch (IOException ioe) {
                LOGGER.error("IO Exception Closing HTTP Client Connection:[{}].",
                        ioe.getMessage(), ioe);
            }
        }
    }

    /**
     * Helper Method to Transform an InputStream to a Byte Array
     * @param inputStream InputStream Reference
     * @return byte[] Array of InputStream Data.
     * @throws IOException Thrown when Condition Raised.
     */
    protected static byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            byte[] b = new byte[8192];
            int n = 0;
            while ((n = inputStream.read(b)) != -1) {
                output.write(b, 0, n);
            }
            return output.toByteArray();
        } finally {
            output.close();
        }
    }

}
