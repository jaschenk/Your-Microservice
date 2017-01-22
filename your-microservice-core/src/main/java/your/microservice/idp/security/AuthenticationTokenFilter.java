package your.microservice.idp.security;

import com.nimbusds.jwt.JWTClaimsSet;

import your.microservice.idp.jwt.YourMicroserviceInvalidTokenException;
import your.microservice.idp.jwt.YourMicroserviceToken;
import your.microservice.idp.repository.IdentityProviderEntityManager;
import your.microservice.idp.service.legacy.YourMicroserviceUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * AuthenticationTokenFilter
 * ~~~~~~~~~~~~~~~~~~~~~~~~~
 * <p>
 * Provides Incoming Request Filter for Processing the JWT, which will equate to
 * an Authenticated User.  Once JWT is validated on the request, UserDetails is instantiated to
 * represent the Authenticated User as well as any Groups/Roles applicable are established as well.
 * <p>
 * Audit Logging will be performed of successful access ensuring recording of timed event.
 * <p>
 * If JWT is not validated, we will perform an Audit Logging of the Event.
 * <p>
 * In either case, the filter chain continues....
 *
 * @author jeff.a.schenk@gmail.com
 */
public class AuthenticationTokenFilter extends UsernamePasswordAuthenticationFilter {
    /**
     * Your Microservice Token Component
     */
    @Autowired
    private YourMicroserviceToken yourMicroserviceToken;
    /**
     * Your Microservice User Details Service
     */
    @Autowired
    private YourMicroserviceUserDetailsService userDetailsService;
    /**
     * Identity Provider Entity Manager.
     */
    @Autowired
    private IdentityProviderEntityManager identityProviderEntityManager;

    /**
     * doFilter
     * Perform Authorization Access via Token Validation.
     *
     * @param request Reference
     * @param response Reference
     * @param chain Filter Chain
     * @throws java.io.IOException Thrown if IO Exceptions.
     * @throws javax.servlet.ServletException Thrown if Servlet Exceptions.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        /**
         * Obtain the JWT from the Authorization Header.
         */
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authToken = YourMicroserviceSecurityConstants.obtainAuthorizationBearerToken(httpRequest);
        /**
         * Now Verify the Token and then, obtain the Subject Claim.
         * Validate we have a username from an extracted token and we are not authenticated,
         * then determine if the Token can be fully validated and has not Expired.
         */
        if (authToken != null) {
            try {
                JWTClaimsSet jwtClaimsSet = yourMicroserviceToken.verifyToken(authToken);
                if (jwtClaimsSet != null) {
                    /**
                     * Obtain our Subject from the Claims Set, which is our UserName, aka Your Microservice Person's
                     * Primary Email.
                     */
                    String username = jwtClaimsSet.getSubject();
                    if (username != null && !username.isEmpty() &&
                            SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        /**
                         * Perform Statistical Metric of a Token being Used.
                         */
                         Integer countUpdated =
                                 identityProviderEntityManager.incrementTokenHistoryUsage(jwtClaimsSet.getJWTID());
                         if (countUpdated == null || countUpdated != 1) {
                             /**
                              * We did not update the Usage Counter, this indicates that either the
                              * Token has Expired, Revoked or in some other state other than Active,
                              * so, immediately fail this token.
                              */
                             SecurityContextHolder.getContext().setAuthentication(null);
                         }
                    }
                }
            } catch (YourMicroserviceInvalidTokenException iste) {
                /**
                 * Do Nothing, as the attempt of the failed Token will be Denied...
                 */
                SecurityContextHolder.getContext().setAuthentication(null);
                YourMicroserviceToken.LOGGER.warn("{}Invalid Token Denying Access.", YourMicroserviceToken.LOGGING_HEADER);
            }
        }
        /**
         * Continue filter chain.
         */
        chain.doFilter(request, response);
    }



}
