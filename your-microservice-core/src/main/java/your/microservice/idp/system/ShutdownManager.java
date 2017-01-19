package your.microservice.idp.system;


/**
 * ShutdownManager
 *
 * @author jeff.a.schenk@gmail.com on 5/18/16.
 */
public interface ShutdownManager {

    void initiateShutdown(String reason, int returnCode);
}
