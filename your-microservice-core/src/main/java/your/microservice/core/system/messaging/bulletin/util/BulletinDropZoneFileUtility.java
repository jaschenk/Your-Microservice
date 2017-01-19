package your.microservice.core.system.messaging.bulletin.util;

import java.io.File;

/**
 * BulletinDropZoneFileUtility
 *
 * @author jeff.a.schenk@gmail.com
 */
public final class BulletinDropZoneFileUtility {

    /**
     * Helper method to determine if Directory is valid.
     *
     * @param zoneDirectory Drop Zone Directory
     * @return boolean indicator - true if Zone directory is Valid.
     */
    public static boolean isZoneDirectoryValid(File zoneDirectory) {
        return zoneDirectory.exists() &&
               zoneDirectory.canRead() &&
               zoneDirectory.canWrite() &&
               zoneDirectory.isDirectory();

    }

}
