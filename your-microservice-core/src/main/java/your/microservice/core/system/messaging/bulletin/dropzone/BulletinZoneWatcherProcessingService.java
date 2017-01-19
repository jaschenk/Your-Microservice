package your.microservice.core.system.messaging.bulletin.dropzone;

/**
 * Bulletin Zone Watcher Processing Service Interface
 *
 * Provides Bulletin Processing Services to Application Framework.
 *
 * @author jeff.a.schenk@gmail.com on 8/28/15.
 */
public interface BulletinZoneWatcherProcessingService {

    /**
     * Provide status of Bulletin Zone Watcher Processing Service.
     *
     * @return String Containing JSON Rendered Status.
     */
    String status();

    /**
     * Provide Running Status
     * @return boolean indicator if Service is running or not.
     */
    boolean isRunning();
}
