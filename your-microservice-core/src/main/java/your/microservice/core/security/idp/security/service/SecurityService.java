package your.microservice.core.security.idp.security.service;

public interface SecurityService {

    Boolean hasProtectedAccess();

    Boolean hasUserAccess();

}
