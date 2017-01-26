package your.microservice.core.security.idp.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * EntryPointUnauthorizedHandler
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <p>
 * Provides Unauthorized Request Handling for any Incoming Request whose JWT does not validate.
 *
 * @author jeff.a.schenk@gmail.com
 */
@Component
public class EntryPointUnauthorizedHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        // TODO :: Log and Send Timed Audit Event.
        httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access Denied");
    }

}
