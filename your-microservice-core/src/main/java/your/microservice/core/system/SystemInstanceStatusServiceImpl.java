package your.microservice.core.system;

import org.springframework.beans.factory.annotation.Autowired;
import your.microservice.core.AppInfo;
import your.microservice.core.dm.dto.system.YourBulletin;
import your.microservice.core.system.messaging.model.YourMSBulletinBroadcastNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Date;

/**
 * SystemInstanceStatusServiceImpl
 * <p>
 * System Instance Status Service,
 * Overall System Status from various other entities and Components
 * are set as additional Instance Metrics.
 *
 * @author jeff.a.schenk@gmail.com on 8/28/15.
 */
@Service("systemInstanceStatusService")
public class SystemInstanceStatusServiceImpl implements SystemInstanceStatusService, HealthIndicator {
    /**
     * Logging
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(SystemInstanceStatusServiceImpl.class);
    /**
     * Static Application Information Object for this Runtime Instance.
     */
    @Autowired
    private AppInfo appInfo;
    /**
     * Constants
     */
    private static final String ALL_SERVICES_OPERATIONAL = "All Services Operational";
    public static final String ENVIRONMENT_PROPERTIES_PREFIX = "FEP."; // Front End Environment Property, not the other FEP!
    /**
     * Health Constants
     */
    private static final String HEALTH_KEY = "System Instance Status Service";
    /**
     * Current System Instance Status
     */
    private YourMSBulletinBroadcastNotification yourMSBulletinBroadcastNotification;
    /**
     * Instance Address, could be null, if unable to determine Address.
     */
    private InetAddress instanceAddress;
    /**
     * Instance Token, could be null, if unable to determine Token.
     */
    private String instanceToken;
    /**
     * Lock Object to ensure everything is synchronized accessing Bulletin, which may Change.
     */
    private final Object LOCK = new Object();

    /**
     * Initialize the Service Provider Interface
     */
    @PostConstruct
    public synchronized void initialize() {
        LOGGER.info("Your Microservice System Instance Status Service Provider Facility is Initializing.");
        /**
         * Obtain our System Instance Address.
         */
        try {
            instanceAddress = InetAddress.getLocalHost();
            instanceToken = bytesToHexString(instanceAddress.getAddress());
            LOGGER.info("Obtain our Running Instance Address:[" +
                    instanceAddress.getHostAddress() + "], Token:[" + instanceToken + "]");
        } catch (UnknownHostException e) {
            LOGGER.warn("Unknown Host Exception obtaining our Local Instance Address: " + e.getMessage());
        }
        /**
         * Reset our Bulletin.
         */
        resetCurrentSystemInstanceStatusBulletin();
    }

    /**
     * Destroy Service
     * Invoked during Termination of the Spring Container.
     */
    @PreDestroy
    public synchronized void destroy() {
        LOGGER.info("Your Microservice System Instance Status Service Provider Facility is Shutting Down.");
    }

    /**
     * getCurrentSystemInstanceStatus
     * <p>
     * Obtain our System Instance Status to report to our Upstream Client Consumers.
     *
     * @return YourMSBulletinBroadcastNotification
     */
    @Override
    public YourMSBulletinBroadcastNotification getCurrentSystemInstanceStatusBulletin() {
        /**
         * Return our Current Bulletin Notification Object.
         */
        synchronized(LOCK) {
            yourMSBulletinBroadcastNotification.setNotificationTime(Date.from(Instant.now()));
            return yourMSBulletinBroadcastNotification;
        }
    }

    /**
     * setCurrentSystemInstanceStatusBulletin
     * <p>
     * Allow External File Entity, namely a Bulletin to be injected into the
     * System Status.  This will allow for this Bulletin Status to be Broadcast
     * to Client Consumers of this System Instance's Services.
     *
     * @param yourBulletin Latest Your Microservice Bulletin Pushed in as External Status
     */
    @Override
    public void setCurrentSystemInstanceStatusBulletin(YourBulletin yourBulletin) {
        synchronized(LOCK) {
            /**
             * Establish new Bulletin Notification State.
             */
            YourMSBulletinBroadcastNotification yourMSBulletinBroadcastNotification =
                    new YourMSBulletinBroadcastNotification(yourBulletin);
            /**
             * Set the Instance Token.
             */
            yourBulletin.setCloudInstance(instanceToken);
            /**
             * Set the Front End and Cloud Instance Versions.
             */
            yourBulletin.setCloudVersion(appInfo.getBuildVersion());
            /**
             * Set new Current
             */
            LOGGER.info("Updating and Setting New System Instance Bulletin Status.");
            this.yourMSBulletinBroadcastNotification = yourMSBulletinBroadcastNotification;
        }
    }

    /**
     * resetCurrentSystemInstanceStatus
     * <p>
     * Reset to Default Status and
     * Obtain our System Instance Status to report to our Upstream Client Consumers.
     *
     * @return YourMSBulletinBroadcastNotification
     */
    @Override
    public YourMSBulletinBroadcastNotification resetCurrentSystemInstanceStatusBulletin() {
        synchronized(LOCK) {
            yourMSBulletinBroadcastNotification = new YourMSBulletinBroadcastNotification();
            YourBulletin yourBulletin = new YourBulletin();
            yourBulletin.setCloudInstance(instanceToken);
            yourBulletin.setCloudVersion(appInfo.getBuildVersion());
            yourBulletin.setCloudStatus(YourBulletin.CloudStatusType.OK.toString());
            yourBulletin.setMessageLevel(YourBulletin.MessageLevel.INFO.toString());
            yourBulletin.setMessageType(YourBulletin.MessageType.CLOUD_BULLETIN.toString());
            yourBulletin.setMessage(ALL_SERVICES_OPERATIONAL);
            yourMSBulletinBroadcastNotification.setYourBulletin(yourBulletin);
            /**
             * Return our Current Bulletin Notification Object.
             */
            return yourMSBulletinBroadcastNotification;
        }
    }

    /**
     * Perform our Health check of checking a connection to ODB.
     *
     * @return Health of our Component
     */
    @Override
    public Health health() {
        synchronized(LOCK) {
            if (yourMSBulletinBroadcastNotification.
                    getYourBulletin().getCloudStatus().equalsIgnoreCase(YourBulletin.CloudStatusType.OK.toString())) {
                return Health.up().withDetail(HEALTH_KEY, getCurrentSystemInstanceStatusBulletin()).build();
            }
            return Health.status(yourMSBulletinBroadcastNotification.getYourBulletin().getCloudStatus())
                    .withDetail(HEALTH_KEY, getCurrentSystemInstanceStatusBulletin()).build();
        }
    }

    /**
     * isCurrentSystemInstanceStatusOK
     * Provide current System Instance State Indication if OK or not.
     *
     * @return boolean indicator if System Status is 'OK' then true, otherwise false.
     */
    @Override
    public boolean isCurrentSystemInstanceStatusOK() {
        synchronized(LOCK) {
            return yourMSBulletinBroadcastNotification.
                    getYourBulletin().getCloudStatus().equalsIgnoreCase(YourBulletin.CloudStatusType.OK.toString());
        }
    }

    /**
     * getCurrentSystemInstanceStatus
     * Provide current System Instance Status.
     *
     * @return String of System Status.
     */
    @Override
    public String getCurrentSystemInstanceStatus() {
        synchronized(LOCK) {
            return yourMSBulletinBroadcastNotification.
                    getYourBulletin().getCloudStatus();
        }
    }

    /**
     * getInstanceAddress
     * Obtain our running Instance Local IP Address.
     *
     * @return InetAddress representing our Local Server-Side IP Address.
     */
    @Override
    public InetAddress getInstanceAddress() {
        return instanceAddress;
    }

    /**
     * getInstanceToken
     * Obtain our running Instance Token.
     *
     * @return String representing our Local Server-Side Token.
     */
    @Override
    public String getInstanceToken() {
        return instanceToken;
    }

    /**
     * Bytes to Hexadecimal String.
     * @param bytes to be convert to HEx string
     * @return String containing Hex String of Byte Array.
     */
    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

}
