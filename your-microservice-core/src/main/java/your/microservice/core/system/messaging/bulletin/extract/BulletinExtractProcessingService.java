package your.microservice.core.system.messaging.bulletin.extract;


/**
 * Extract Processing Service Interface
 *
 * Provides Extract Processing Services to Application Framework.
 */
public interface BulletinExtractProcessingService {

    /**
     * Perform the Extract LifeCycle.
     * @param dropZoneFileDirectoryName Directory Name where Bulletin File(s)
     *                                  should be consumed.
     */
    void performExtractLifeCycle(String dropZoneFileDirectoryName);

    /**
     * Provide status of Bulletin Zone Watcher Processing Service.
     *
     * @return String Containing JSON Rendered Status.
     */
    String status();

}
