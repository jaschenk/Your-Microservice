package your.microservice.core.security.idp.service;

public interface SecurityService {

    Boolean hasProtectedAccess();

    Boolean hasUserAccess();

}
