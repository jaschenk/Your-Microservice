package your.microservice.idp.service.legacy;

import org.springframework.security.core.AuthenticationException;

/**
 *
 */
class RateExceededException extends AuthenticationException {
    public RateExceededException(String message) {
        super(message);
    }
}
