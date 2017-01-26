package your.microservice.core.security.idp.security;

import com.nimbusds.jwt.JWTClaimsSet;

import your.microservice.core.system.messaging.jms.MessagePublisherService;
import your.microservice.core.security.idp.jwt.YourMicroserviceInvalidTokenException;
import your.microservice.core.security.idp.jwt.YourMicroserviceToken;
import your.microservice.core.security.idp.model.base.YourEntityEventHistory;
import your.microservice.core.security.idp.model.base.YourEntityTokenHistory;
import your.microservice.core.security.idp.model.json.request.AuthenticationRequest;
import your.microservice.core.security.idp.model.json.response.AuthenticationResponse;
import your.microservice.core.security.idp.model.security.YourMicroserviceUserDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.web.bind.annotation.*;
import your.microservice.core.security.idp.model.types.YourEntityTokenStatus;
import your.microservice.core.security.idp.repository.IdentityProviderEntityManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * AuthenticationController
 * ~~~~~~~~~~~~~~~~~~~~~~~~~
 * <p>
 * Provides Incoming Request End points for performing necessary Authentication Processing for obtaining
 * a Your Microservice Signed JWT for further Access into the Your Microservice Eco-System.
 *
 * @author jeff.a.schenk@gmail.com
 */
@RestController
@RequestMapping("${your.microservice.security.route.authentication}")
@Api(basePath = "/api/auth", value = "Authentication", description = "Your Microservice IO IdP Authentication Methods", produces = "application/json")
public class AuthenticationController {

    /**
     * Common Logger
     */
    protected final static org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(AuthenticationController.class);

    /**
     * TOKEN_EXPIRATION_IN_SECONDS
     *
     * Expiration of Tokens in Seconds.
     * Example specified as '14400' equates to 4 Hours.
     */
    @Value("${your.microservice.security.token.expiration}")
    private Long TOKEN_EXPIRATION_IN_SECONDS = 14400L;

    /**
     * Authentication Manager
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Your Microservice Token Component
     */
    @Autowired
    private YourMicroserviceToken yourMicroserviceToken;

    /**
     * Your Microservice User Details
     */
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Message Publication Service.
     */
    @Autowired
    private MessagePublisherService messagePublisherService;

    /**
     * Identity Provider Entity Manager.
     */
    @Autowired
    private IdentityProviderEntityManager identityProviderEntityManager;

    /**
     * authenticationRequest
     * Authentication Request to acquire an Access Token, to then in turn use this Token to access a
     * protected resource within the Your Microservice Eco-System.
     *
     * @param authenticationRequest Incoming Authentication Request DTO
     * @param device                String Identifier indicating Device Type from which request has been originated.
     * @param request               Incoming Request
     * @return ResponseEntity If successful Authentication, Response Entity will Contain the JWT to be used for accessing
     * subsequent Protected resources within the Your Microservice Eco-System.
     * @throws AuthenticationException Thrown when Failure Occurs, Handlers will Response Accordingly.
     */
    @ApiOperation(value="Authentication to obtain Access Token", httpMethod = "POST", hidden = true)
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> authenticationRequest(@RequestBody AuthenticationRequest authenticationRequest,
                                                   Device device,
                                                   HttpServletRequest request) throws AuthenticationException {
        /**
         * Perform Authentication Request for Consumer to Obtain Access Token.
         */
        LOGGER.info("Performing Authentication of '{}' from '{}'", authenticationRequest.getUsername(), device);
        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Reload password post-authentication so we can generate token
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        String token = this.yourMicroserviceToken.generateToken(userDetails.getUsername(),
                yourMicroserviceToken.transformAudienceType(device));
        /**
         * Publish Authentication Notification...
         */
        try {
            publishAuthenticationEvents(request, (YourMicroserviceUserDetails)userDetails,
                    YourEntityEventHistory.EntityEventTagNames.LAST_LOGIN);
        } catch (Exception e) {
            LOGGER.warn("Unable to perform Publishing of Entity Event History for Last Login: {}",
                    e.getMessage());
        }
        /**
         * Persist our new Token Entity to our History.
         */
        saveTokenHistory(token);
        /**
         * Return the Successful response with our Generated Access Token.
         */
        return ResponseEntity.ok(new AuthenticationResponse(token, TOKEN_EXPIRATION_IN_SECONDS.intValue()));
    }

    /**
     * authenticationRequest
     * Authentication Request to acquire an Access Token, to then in turn use this Token to access a
     * protected resource within the Your Microservice Eco-System.
     *
     * @param authenticationRequest Incoming Authentication Request DTO
     * @param request               Incoming Request
     * @return ResponseEntity If successful Authentication, Response Entity will Contain the JWT to be used for accessing
     * subsequent Protected resources within the Your Microservice Eco-System.
     * @throws AuthenticationException Thrown when Failure Occurs, Handlers will Response Accordingly.
     */
    @ApiOperation(value="Authentication to obtain Access Token using Swagger Console",
            httpMethod = "POST", hidden = false)
    @RequestMapping(value="/", method = RequestMethod.POST)
    public ResponseEntity<?> swaggerAuthenticationRequest(@RequestBody AuthenticationRequest authenticationRequest,
                                                   HttpServletRequest request) throws AuthenticationException {
        /**
         * Perform Authentication Request for Consumer to Obtain Access Token.
         */
        LOGGER.info("Performing Authentication of '{}' from '{}'", authenticationRequest.getUsername(), "swagger");
        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Reload password post-authentication so we can generate token
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        String token = this.yourMicroserviceToken.generateToken(userDetails.getUsername(), "unknown");
        /**
         * Publish Authentication Notification...
         */
        try {
            publishAuthenticationEvents(request, (YourMicroserviceUserDetails)userDetails,
                    YourEntityEventHistory.EntityEventTagNames.LAST_LOGIN);
        } catch (Exception e) {
            LOGGER.warn("Unable to perform Publishing of Personal Event History for Last Login: {}",
                    e.getMessage());
        }
        /**
         * Persist our new Token Entity to our History.
         */
        saveTokenHistory(token);
        /**
         * Return the Successful response with our Generated Access Token.
         */
        return ResponseEntity.ok(new AuthenticationResponse(token, TOKEN_EXPIRATION_IN_SECONDS.intValue()));
    }


    /**
     * authenticationRequest
     * Requesting a Refresh of an Existing Access Token.
     *
     * @param request Incoming HTTP Request to perform a Refresh of the Entities Existing Access Token.
     * @return ResponseEntity If successful Token Validation and is not Expired,
     * Response Entity will Contain the JWT to be used for accessing
     * subsequent Protected resources within the Your Microservice Eco-System.
     */
    @ApiOperation(value="Auth Refresh Access Token", httpMethod = "GET")
    @RequestMapping(value = "${your.microservice.security.route.authentication.refresh}", method = RequestMethod.GET)
    public ResponseEntity<?> refreshRequest(HttpServletRequest request) {

        String token = YourMicroserviceSecurityConstants.obtainAuthorizationBearerToken(request);
        String username = this.yourMicroserviceToken.getUsernameFromToken(token);
        try {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            /**
             * For now we Assume we can perform a Refresh, regardless ot Expiration...
             */
            String refreshedToken = this.yourMicroserviceToken.refreshToken(token);
            /**
             * Publish Refresh Token Notification...
             */
            try {
                publishAuthenticationEvents(request, (YourMicroserviceUserDetails)userDetails,
                        YourEntityEventHistory.EntityEventTagNames.LAST_TOKEN_REFRESH);
            } catch (Exception e) {
                LOGGER.warn("Unable to perform Publishing of Personal Event History for Last Token Refresh: {}",
                        e.getMessage());
            }
            /**
             * Persist our new Token Entity to our History.
             */
            saveTokenHistory(refreshedToken);
            /**
             * Return the Successful response with our newly Generated Access Token.
             */
            return ResponseEntity.ok(new AuthenticationResponse(refreshedToken, TOKEN_EXPIRATION_IN_SECONDS.intValue()));

        } catch (UsernameNotFoundException une) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * authenticationRequest
     * Requesting a Revocation of an Existing Access Token.
     *
     * @param request Incoming HTTP Request to perform a Logout of the Entities Existing Access Token.
     * @return ResponseEntity If successful will be null.
     */
    @ApiOperation(value="Auth Logout and Revoke Access Token", httpMethod = "GET")
    @RequestMapping(value = "${your.microservice.security.route.authentication.logout}", method = RequestMethod.GET)
    public ResponseEntity<?> logoutAndRevokeAccess(HttpServletRequest request) {

        String token = YourMicroserviceSecurityConstants.obtainAuthorizationBearerToken(request);
        String username = this.yourMicroserviceToken.getUsernameFromToken(token);
        try {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            /**
             * Perform a Token Delete to ensure Token is no longer available for Use.
             */
            String jti = this.yourMicroserviceToken.getIdentifierFromToken(token);
            Integer deleted =
                    identityProviderEntityManager.deleteTokenHistory(jti);
            if (deleted == null || deleted <=0) {
                /**
                 * The Deletion Failed!
                 */
                LOGGER.warn("Issue Deleting Token:'{}' Used By:'{}', Ignoring Logout.", jti, username);
                return ResponseEntity.badRequest().body(null);
            } else {
                /**
                 * The Deletion Successful!
                 */
                LOGGER.info("Logout Successful Token:'{}' Used By:'{}'.", jti, username);
                /**
                 * Publish Personal Event History Refresh Token Notification...
                 */
                try {
                    publishAuthenticationEvents(request, (YourMicroserviceUserDetails) userDetails,
                            YourEntityEventHistory.EntityEventTagNames.LAST_LOGOUT);
                } catch (Exception e) {
                    LOGGER.warn("Unable to perform Publishing of Personal Event History for Last Logout: {}",
                            e.getMessage());
                }
                /**
                 * Return the Successful response of the revocation of an existing Token.
                 */
                return ResponseEntity.ok().body(null);
            }
        } catch (UsernameNotFoundException une) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @ExceptionHandler({org.springframework.http.converter.HttpMessageNotReadableException.class,
            PreAuthenticatedCredentialsNotFoundException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)  // 400
    @ResponseBody
    public String resolveBadRequestExceptions() {
        return "error";
    }

    @ExceptionHandler({BadCredentialsException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)  // 401
    @ResponseBody
    public String resolveBadCredentialsExceptions() {
        return "unauthorized";
    }

    @ExceptionHandler({AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN) // 403
    @ResponseBody
    public String resolveAccessDeniedExceptions() {
        return "forbidden";
    }

    /**
     * publishAuthenticationEvents
     *
     * @param request     Request reference
     * @param userDetails UserDetailsReference Object
     * @param eventTag Type of Event to be Published.
     * @throws java.io.IOException      - Throws If IOException
     * @throws javax.servlet.ServletException - Throws if Servlet Exception Detected.
     */
    protected void publishAuthenticationEvents(HttpServletRequest request, YourMicroserviceUserDetails userDetails,
                                               YourEntityEventHistory.EntityEventTagNames eventTag)
            throws IOException, ServletException {
        /**
         * Check to ensure all Parameters are Available?
         */
        if (request == null || userDetails == null || eventTag == null) {
            return;
        }
        /**
         * WA-500
         * Publish our Successful Login Event.
         */
        Map<String, String> eventTagProperties = new HashMap<>();
        /**
         * Save All Applicable Headers, assume defaults...
         */
        String requestSourceAddress = request.getRemoteAddr();
        String requestSourcePort = Integer.toString(request.getRemotePort());
        String requestSourceProtocol = request.getProtocol();
        /**
         * Check for Where request originated if we are behind a Load Balancer.
         */
        if (request.getHeader(YourEntityEventHistory.PROPERTY_TAG_NAME_XFORWARDED_FOR) != null) {
            eventTagProperties.put(YourEntityEventHistory.PROPERTY_TAG_NAME_XFORWARDED_FOR,
                    request.getHeader(YourEntityEventHistory.PROPERTY_TAG_NAME_XFORWARDED_FOR));
            requestSourceAddress = eventTagProperties.get(YourEntityEventHistory.PROPERTY_TAG_NAME_XFORWARDED_FOR);
        }
        if (request.getHeader(YourEntityEventHistory.PROPERTY_TAG_NAME_XFORWARDED_PROTO) != null) {
            eventTagProperties.put(YourEntityEventHistory.PROPERTY_TAG_NAME_XFORWARDED_PROTO,
                    request.getHeader(YourEntityEventHistory.PROPERTY_TAG_NAME_XFORWARDED_PROTO));
        }
        if (request.getHeader(YourEntityEventHistory.PROPERTY_TAG_NAME_XFORWARDED_PORT) != null) {
            eventTagProperties.put(YourEntityEventHistory.PROPERTY_TAG_NAME_XFORWARDED_PORT,
                    request.getHeader(YourEntityEventHistory.PROPERTY_TAG_NAME_XFORWARDED_PORT));
        }
        if (request.getHeader(YourEntityEventHistory.PROPERTY_TAG_NAME_HOST) != null) {
            eventTagProperties.put(YourEntityEventHistory.PROPERTY_TAG_NAME_HOST,
                    request.getHeader(YourEntityEventHistory.PROPERTY_TAG_NAME_HOST));
        }
        if (request.getHeader(YourEntityEventHistory.PROPERTY_TAG_NAME_USER_AGENT) != null) {
            eventTagProperties.put(YourEntityEventHistory.PROPERTY_TAG_NAME_USER_AGENT,
                    request.getHeader(YourEntityEventHistory.PROPERTY_TAG_NAME_USER_AGENT));
        }
        /**
         * Construct the Message
         */
        StringBuilder eventMessage = new StringBuilder();
        eventMessage.append(eventTag.name());
        eventMessage.append(" from ").append(requestSourceAddress).
                append(" port: ").append(requestSourcePort).
                append(" Protocol: ").append(requestSourceProtocol);
        /**
         * Instantiate the Person Event History And Publish the Event.
         */
        YourEntityEventHistory yourEntityEventHistory = new YourEntityEventHistory(
                eventTag.name(),
                eventMessage.toString(), eventTagProperties);
        messagePublisherService.publishEntityEventHistory(userDetails.getPrincipalID(),
               yourEntityEventHistory);
    }

    /**
     * saveTokenHistory
     *
     * @param token Token to re-verify to obtain Claims Set to Persist as a Token History Element.
     */
    @Async
    protected void saveTokenHistory(String token) {
        try {
            /**
             * Generate a Token History Entry based upon our Current Supplied Token.
             */
            JWTClaimsSet claimsSet = yourMicroserviceToken.verifyToken(token);
            if (claimsSet == null) {
                LOGGER.warn("Unable to Verify Token to retrieve ClaimsSet to Persist Token History, Ignoring.");
                return;
            }
            /**
             * Instantiate the Token History Entity.
             */
            YourEntityTokenHistory yourEntityTokenHistory = new YourEntityTokenHistory();
            yourEntityTokenHistory.setJti(claimsSet.getJWTID());
            yourEntityTokenHistory.setSubject(claimsSet.getSubject());
            yourEntityTokenHistory.setStatus(YourEntityTokenStatus.ACTIVE);
            yourEntityTokenHistory.setIssuedAt(claimsSet.getIssueTime());
            yourEntityTokenHistory.setExpiration(claimsSet.getExpirationTime());
            yourEntityTokenHistory.setNotUsedBefore(claimsSet.getNotBeforeTime());
            yourEntityTokenHistory.setLastUsed(claimsSet.getIssueTime());
            yourEntityTokenHistory.setUsageCount(1L);
            /**
             * Persist the Entity.
             */
            yourEntityTokenHistory = identityProviderEntityManager.createTokenHistory(yourEntityTokenHistory);
            if (yourEntityTokenHistory == null) {
                LOGGER.warn("Unable to Persist Token History Entity, Ignoring.");
            }
        } catch (YourMicroserviceInvalidTokenException ite) {
            LOGGER.warn("Invalid Your Microservice Token Exception:'{}', Encountered while attempting " +
                    "to persist Token History Entity.", ite.getMessage(), ite);
        }
    }

}
