package your.microservice.core.security.idp.service.legacy;

import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * YourMSWebAuthenticationDetailsSource
 *
 * @author jeff.a.schenk@gmail.com on 11/14/15.
 */
@Component
public class YourMSWebAuthenticationDetailsSource extends WebAuthenticationDetailsSource {

    @Override
    public WebAuthenticationDetails buildDetails(HttpServletRequest context) {
        return new YourMicroserviceWebAuthenticationDetails(context);
    }

    @SuppressWarnings("serial")
    class YourMicroserviceWebAuthenticationDetails extends WebAuthenticationDetails {

        private final String referer;

        public YourMicroserviceWebAuthenticationDetails(HttpServletRequest request) {
            super(request);
            this.referer = request.getHeader("Referer");
        }

        public String getReferer() {
            return referer;
        }
    }

}
