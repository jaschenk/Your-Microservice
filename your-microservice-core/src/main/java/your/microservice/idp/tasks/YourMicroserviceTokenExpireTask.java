package your.microservice.idp.tasks;

/**
 * YourMicroserviceTokenExpireTask
 *
 * Provides the Interface for the Your Microservice Token Expiration Task,
 * which will delete and purge any expired tokens.
 *
 *
 * @author jeff.a.schenk@gmail.com on 7/26/16.
 */
public interface YourMicroserviceTokenExpireTask {

    /**
     * Perform the Physical Purge of any and all Expired Tokens.
     */
    void purgeExpiredTokens();

}
