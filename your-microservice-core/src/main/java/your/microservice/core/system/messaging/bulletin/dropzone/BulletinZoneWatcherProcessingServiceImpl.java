package your.microservice.core.system.messaging.bulletin.dropzone;


import your.microservice.core.system.messaging.bulletin.extract.BulletinExtractProcessingService;
import your.microservice.core.system.messaging.bulletin.util.BulletinDropZoneFileUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

/**
 * Bulletin Zone Watcher Processing Service Implementation
 *
 * @author Jeff Schenk jeff.a.schenk@gmail.com
 */
@Service("bulletinZoneWatcherProcessingService")
public class BulletinZoneWatcherProcessingServiceImpl implements BulletinZoneWatcherProcessingService {
    /**
     * Logging
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(BulletinZoneWatcherProcessingServiceImpl.class);

    /**
     * Initialization Indicator.
     */
    private boolean initialized = false;

    /**
     * Constant and Property Variable
     * for Bulletin Drop Zone.
     */
    private static final String DEFAULT_BULLETIN_DIRECTORY_NAME = "bulletin_messaging_dropzone";
    public static final String BULLETIN_DROPZONE_DIRECTORY_PROPERTY_NAME = "bulletin.dropzone.directory";
    /**
     * Current Bulletin Drop Zone File Directory Name.
     */
    private String dropZoneFileDirectoryName;

    @Autowired
    private Environment environment;

    /**
     * Extract Service
     */
    @Autowired
    private BulletinExtractProcessingService bulletinExtractProcessingService;

    /**
     * Task Executor
     */
    @Autowired
    @Qualifier("bulletinZoneWatcherThreadPoolTaskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    /**
     * Zone Watcher Thread Object
     */
    private ZoneWatcherTask zoneWatcherTask;

    /**
     * Singleton Globals for Watch Service
     */
    private Path directoryPath;
    private WatchService watcher;

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    /**
     * Initialize the Service Provider Interface
     */
    @PostConstruct
    public synchronized void initialize() {
        LOGGER.info("Your Microservice Bulletin Zone Watcher Service Provider Facility is Initializing.");
        /**
         * Initialize and
         * Obtain our Environment Runtime Properties.
         */
        //Properties environmentProperties = PropLoader.getProps();



        if (environment.getProperty(BULLETIN_DROPZONE_DIRECTORY_PROPERTY_NAME)==null) {
            /**
             * Formulate a Default Bulletin Drop Zone in Temporary File System Storage.
             */
            formulateTemporaryBulletinDropZone();
        } else {
            dropZoneFileDirectoryName = environment.getProperty(BULLETIN_DROPZONE_DIRECTORY_PROPERTY_NAME);
            if (! BulletinDropZoneFileUtility.isZoneDirectoryValid(new File(dropZoneFileDirectoryName))) {
                formulateTemporaryBulletinDropZone();
            }
        }
        /**
         * Determine if we can continue?
         */
        if (dropZoneFileDirectoryName == null) {
            LOGGER.error("Unable to construct a valid Bulletin Drop Zone Directory, please check Properties!");
            return;
        }
        /**
         * Cleanup any Existing Bulletin Files
         */
        cleanUpPreviousBulletinFiles();
        /**
         * Now Establishing our File Watcher for any newly Created Files in our
         * specified Bulletin Drop Zone.
         */
        try {
            this.watcher = FileSystems.getDefault().newWatchService();
            /**
             * Register the Drop Zone Directory to Monitor
             */
            this.directoryPath = Paths.get(dropZoneFileDirectoryName);
            this.directoryPath.register(this.watcher, ENTRY_CREATE);
            /**
             * Now Start the Actual Service Worker Thread to perform the Watching Facility.
             */
            zoneWatcherTask = new ZoneWatcherTask();
            this.taskExecutor.execute(zoneWatcherTask);
            this.initialized = true;
            LOGGER.info("Your Microservice Bulletin Zone Watcher Service Provider Facility is Ready and Available.");
            LOGGER.info(" + Your Microservice Bulletin Drop Zone Directory being watched:["+dropZoneFileDirectoryName+"]");
        } catch (IOException ioe) {
            LOGGER.error("Issue Establishing File System Watch Service, unable to provide Zone Watcher Services!", ioe);
        }
    }

    /**
     * Destroy Service
     * Invoked during Termination of the Spring Container.
     */
    @PreDestroy
    public synchronized void destroy() {
        try {
            if (this.initialized) {
                if (this.zoneWatcherTask != null) {
                    this.zoneWatcherTask.setStopProcess(true);
                    this.watcher.close();
                }
                LOGGER.info("Your Microservice Bulletin Zone Watcher Service Provider Facility has been Shutdown.");
            }
        } catch (IOException ioe) {
            LOGGER.error("Issue Finalizing File System Watch Service!", ioe);
        }
    }

    /**
     * Provide status of Zone Watcher Processing Service.
     * @return String JSON Rendered Status.
     */
    @Override
    public String status() {
        StringBuilder sb = new StringBuilder();
        // TODO

        return sb.toString();
    }

    /**
     * Provide Running Status
     */
    public boolean isRunning() {
        return this.initialized;
    }

    /**
     * cleanUpPreviousBulletinFiles
     */
    protected void cleanUpPreviousBulletinFiles() {
        if (dropZoneFileDirectoryName == null) {
            return;
        }
        File dropZoneDirectory = new File(dropZoneFileDirectoryName);
        for (File extractFile : dropZoneDirectory.listFiles()) {
            if (extractFile.delete()) {
                LOGGER.info("Removed Previous Your Microservice Bulletin File:[" + extractFile.getAbsolutePath() + "]");
            }
        }
    }

    /**
     * Helper Method to Prepare a Temporary Bulletin Drop Zone.
     */
    protected void formulateTemporaryBulletinDropZone() {
        File temp = null;
        try{
            temp = File.createTempFile(DEFAULT_BULLETIN_DIRECTORY_NAME, ".directory");
            /**
             * Remove the file for now ...
             */
            if (temp.exists()) {
                temp.delete();
            }
            /**
             * Now Create the Temporary Directory
             */
            temp.mkdirs();
        } catch(IOException ioe) {
            LOGGER.error("Attempting Creation of Temporary Bulletin Dropzone Directory Failed: {}",
                    ioe.getMessage(),ioe);
            return;
        }

        if (BulletinDropZoneFileUtility.isZoneDirectoryValid(temp)) {
            dropZoneFileDirectoryName = temp.getAbsolutePath();
            LOGGER.info("Temporary Bulletin Dropzone Directory:[{}] has been created for this Runtime Instance.",
                    dropZoneFileDirectoryName);
            LOGGER.info("If you would like to reuse this Temporary Dropzone, " +
                    "assign the Dropzone Property with the appropriate File Directory Path Value.");
        } else {
          LOGGER.error("Attempted to construct a Temporary Directory Named:["+temp.getAbsolutePath()+
            "], however, not a valid Bulletin Drop Zone Directory.");
            dropZoneFileDirectoryName = null;
        }

    }

    /**
     * Zone Watcher Task Thread.
     */
    private class ZoneWatcherTask implements Runnable {

        private boolean stopProcess = false;

        private ZoneWatcherTask() {
        }

        protected boolean isStopProcess() {
            return stopProcess;
        }

        protected void setStopProcess(boolean stopProcess) {
            this.stopProcess = stopProcess;
        }

        /**
         * Perform Bulletin Drop Zone Watcher Task.
         */
        public void run() {
            try {
                /**
                 * Avoid a Spring Bug, which would cause a Hang
                 * when we publish an event too quickly at startup.
                 */
                Thread.sleep(10 * 1000);
            } catch (InterruptedException x) {
                // NoOp
            }
            /** ************************************
             * Begin Zone Thread Loop.
             */
            while (true) {
                if (isStopProcess()) {
                    break;
                }
                /** *****************************************
                 * Wait for key to be signalled based upon
                 * Registered Events to appear
                 */
                WatchKey watchKey;
                try {
                    watchKey = watcher.take();
                } catch (InterruptedException x) {
                    if (isStopProcess()) {
                        break;
                    }
                    return;
                } catch (ClosedWatchServiceException cwse) {
                    LOGGER.info("Received Closed Watch Service Exception, assuming Container is Shutting Down.");
                    return;
                }

                /** ****************************************
                 * Poll for Events
                 */
                for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
                    if (isStopProcess()) {
                        break;
                    }
                    WatchEvent.Kind kind = watchEvent.kind();
                    /**
                     * We have Overflowed the event Stack,
                     * for now Ignore.
                     */
                    if (kind == OVERFLOW) {
                        LOGGER.warn("Overflow has occurred on Watcher Event Stack, " +
                                "some Events may have been Lost and not Processed!");
                        continue;
                    }
                    /**
                     * Context for directory entry event is the file name of entry
                     */
                    WatchEvent<Path> ev = cast(watchEvent);
                    Path name = ev.context();
                    Path child = directoryPath.resolve(name);
                    /**
                     * Log the Event
                     */
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Zone Watcher Event:[" + kind.name() + "], File:[" + child + "]");
                    }
                    /**
                     * Perform the Extract,
                     * ---- which will Parse the Bulletin Message File to a POJO
                     * ---- Push that POJO as a new Status Event to the SystemInstanceStatusService.
                     */
                    bulletinExtractProcessingService.performExtractLifeCycle(dropZoneFileDirectoryName);
                } // End of Inner Polling Loop per Key
                /**
                 * Reset our Watcher Key and verify we
                 * have a still valid Watcher Path.
                 */
                if (!watchKey.reset()) {
                    LOGGER.warn("Current WatchKey is No Longer Valid, Disabling the Zone Watcher Thread!");
                    break;
                }
            } // Outer while Loop
            LOGGER.warn("Zone Watcher Processing Thread has Ended.");
        } // End of run Method

    } /// End of Inner Class
}
