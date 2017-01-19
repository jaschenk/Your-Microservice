package your.microservice.idp.service.legacy;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Service;
import your.microservice.idp.security.SecurityRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

/**
 *
 */
@Service
public class YourMSAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(YourMSAuthenticationFailureHandler.class);
    private static final String ERROR_WHILE_ATTEMPTING_RATE_LIMITING_MESSAGE = "Error while attempting rate limiting";
    private static final String COULD_NOT_SEND_NOTIFICATION_EMAIL_MESSAGE = "Could not send notification email";

    /**
     * Full Spring Enable Environment,
     * replaces Old PropLoader.
     */
    @Autowired
    private Environment environment;


    @Autowired
    private SecurityRepository securityRepo;

    public void setSecurityRepository(SecurityRepository securityRepo) {
        this.securityRepo = securityRepo;
    }

    public static String rateLimiterBlock = "RateLimiterBlock";
    private final static double maxRate = 1.0; //requests per second...

    public YourMSAuthenticationFailureHandler() {
    }

    private void coolDown() {
        // TODO NOOP
    }

    private boolean isRateExceeded(double newRate) {
        return newRate >= maxRate;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException ae) throws IOException, ServletException {

        request.getSession().setAttribute("SPRING_SECURITY_LAST_EXCEPTION", ae);
        response.setStatus(SC_UNAUTHORIZED);

        if (ae instanceof RateExceededException) //blocked, check for cooldown
        {
            coolDown();
            return;
        }

        try {
            String ipAddy = request.getRemoteAddr();
            Date now = new Date();
            //double newRate = securityRepo.incrementIp(ipAddy, rateLimiterBlock, now);

            //if (isRateExceeded(newRate)) {
            //    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                //securityRepo.blockIp(ipAddy, rateLimiterBlock);

            //    try {
                    //sendNotificationEmail(ipAddy, newRate);
            //    } catch (Exception ex) {
            //        logger.error(COULD_NOT_SEND_NOTIFICATION_EMAIL_MESSAGE, ex);
            //    }
            //}

        } catch (Exception ex) {
            logger.error(ERROR_WHILE_ATTEMPTING_RATE_LIMITING_MESSAGE, ex);
        }

        //request.getRequestDispatcher("/s").forward(request, response);
    }
}
