package your.microservice.core.system.messaging.bulletin.extract;

import com.codahale.metrics.Counter;
import com.codahale.metrics.annotation.Metric;
import com.fasterxml.jackson.databind.ObjectMapper;
import your.microservice.core.dm.dto.system.YourBulletin;
import your.microservice.core.system.SystemInstanceStatusService;
import your.microservice.core.system.messaging.bulletin.util.BulletinDropZoneFileUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;

/**
 * Extract processing Service Implementation
 *
 * @author Jeff Schenk jeff.a.schenk@gmail.com
 */
@Service("bulletinExtractProcessingService")
public class BulletinExtractProcessingServiceImpl implements BulletinExtractProcessingService {
    /**
     * Logging
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(BulletinExtractProcessingServiceImpl.class);
    /**
     * Object Mapper
     */
    private final ObjectMapper mapper = new ObjectMapper();
    /**
     * Initialization Indicator.
     */
    private boolean initialized = false;
    /**
     * System Instance Status Service Reference.
     */
    @Autowired
    private SystemInstanceStatusService systemInstanceStatusService;
    /**
     * Global Statistic Counters
     */
    /**
     * Metric Counters.
     */
    @Metric(name = "counter.your.microservice.system.messaging.bulletin.extract.files.processed", absolute = true)
    Counter numberOfFileProcessed = new Counter();
    @Metric(name = "counter..your.microservice.system.messaging.bulletin.extract.file.errors", absolute = true)
    Counter numberOfFileErrors = new Counter();
    @Metric(name = "counter..your.microservice.system.messaging.bulletin.extract.files.deleted", absolute = true)
    Counter numberOfFilesDeleted = new Counter();

    /**
     * Initialize the Service Provider Interface
     */
    @PostConstruct
    public synchronized void initialize() {
        LOGGER.info("Extract Processing Service Provider Facility is Ready and Available.");
        this.initialized = true;
    }

    /**
     * Destroy Service
     * Invoked during Termination of the Spring Container.
     */
    @PreDestroy
    public synchronized void destroy() {
        if (this.initialized) {
            LOGGER.info("Extract Processing Service Provider Facility has been Shutdown.");
        }
    }

    /**
     * Provide status of Bulletin Zone Watcher Processing Service.
     *
     * @return String Containing JSON Status.
     */
    @Override
    public String status() {
        StringBuilder sb = new StringBuilder();

        return sb.toString();
    }

    /**
     * Perform the Extract LifeCycle.
     *
     * @param dropZoneFileDirectoryName Directory Name where Bulletin File(s)
     *                                  should be consumed.
     */
    @Override
    public synchronized void performExtractLifeCycle(String dropZoneFileDirectoryName) {
        /**
         * Perform the Main File Processing Loop.
         * Acquire drop zone directory.
         */
        File dropZoneDirectory = new File(dropZoneFileDirectoryName);
        if (!BulletinDropZoneFileUtility.isZoneDirectoryValid(dropZoneDirectory)) {
            LOGGER.warn("Unable to process Extract Files due to Drop zone Directory:["
                    + dropZoneDirectory.getAbsolutePath() + "] is Invalid!");
            return;
        }
        /** ************************************
         * Before we launch directly in, lets us
         * wait for a couple of ticks.  As, we
         * need to ensure all of the data is
         * written to the file before we start
         * processing.  This is due to the fact
         * that the Zone Watcher is popped as soon
         * as first byte is written to file, so
         * we need a tick or two before we start
         * processing, otherwise we will not
         * be able to parse the file.
         */
        try {
            Thread.sleep(5*1000);
        } catch(InterruptedException ie) {
            // Do nothing...
        }
        /** ************************************
         * Begin File Loop
         */
        if (dropZoneDirectory.listFiles() == null) {
            return;
        }
        for (File jsonFile : dropZoneDirectory.listFiles()) {
            LOGGER.info("Found Bulletin Message to Post to System Instance Status in File:["
                    + jsonFile.getAbsolutePath() +"]");
            numberOfFileProcessed.inc();
            try {
                /**
                 * Read and Parse the File.
                 */
                LOGGER.info("Parsing Bulletin Message JSON File:["+jsonFile.getAbsolutePath()+
                        "], Size:["+jsonFile.length()+"b]");
                YourBulletin yourBulletin = mapper.readValue(jsonFile, YourBulletin.class);
                LOGGER.info("Posting Bulletin Message JSON:["+ yourBulletin +"]");
                /**
                 * Post Bulletin Object to Cloud Service State.
                 */
                systemInstanceStatusService.setCurrentSystemInstanceStatusBulletin(yourBulletin);
            } catch (IOException ioe) {
                LOGGER.error("Exception encountered processing Bulletin File:[" + jsonFile.getAbsolutePath() + "], " +
                        ioe.getMessage());
                numberOfFileErrors.inc();
            }
            /**
             * Remove the File Processed.
             */
            if (jsonFile.delete()) {
                LOGGER.info("Removing Processed Bulletin Message File:[" + jsonFile.getAbsolutePath() + "]");
                numberOfFilesDeleted.inc();
            }
        } // End of Extract File located in the Drop zone For Loop.
    }

}
