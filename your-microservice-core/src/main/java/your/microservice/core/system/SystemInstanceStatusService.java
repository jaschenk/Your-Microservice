package your.microservice.core.system;

import your.microservice.core.dm.dto.system.YourBulletin;
import your.microservice.core.system.messaging.model.YourMSBulletinBroadcastNotification;

import java.net.InetAddress;

/**
 * SystemInstanceStatusService
 *
 * @author jeff.a.schenk@gmail.com on 8/28/15.
 */
public interface SystemInstanceStatusService {

    /**
     * getCurrentSystemInstanceStatusBulletin
     *
     * Obtain our System Instance Status to report to our Upstream Client Consumers.
     *
     * @return YourMSBulletinBroadcastNotification
     */
    YourMSBulletinBroadcastNotification getCurrentSystemInstanceStatusBulletin();

    /**
     * setCurrentSystemInstanceStatusBulletin
     *
     * Allow External File Entity, namely a Bulletin to be injected into the
     * System Status.  This will allow for this Bulletin Status to be Broadcast
     * to Client Consumers of this System Instance's Services.
     *
     * @param yourBulletin Latest Your Microservice Bulletin Pushed in as External Status
     */
    void setCurrentSystemInstanceStatusBulletin(YourBulletin yourBulletin);

    /**
     * resetCurrentSystemInstanceStatusBulletin
     *
     * Reset to Default Status and
     * Obtain our System Instance Status to report to our Upstream Client Consumers.
     *
     * @return YourMSBulletinBroadcastNotification
     */
    YourMSBulletinBroadcastNotification resetCurrentSystemInstanceStatusBulletin();

    /**
     * isCurrentSystemInstanceStatusOK
     * Provide current System Instance State Indication if OK or not.
     * @return boolean indicator if System Status is 'OK' then true, otherwise false.
     */
    boolean isCurrentSystemInstanceStatusOK();

    /**
     * getCurrentSystemInstanceStatus
     * Provide current System Instance Status.
     * @return String of System Status.
     */
    String getCurrentSystemInstanceStatus();

    /**
     * getInstanceAddress
     * Obtain our running Instance Local IP Address.
     * @return InetAddress representing our Local Server-Side IP Address.
     */
    InetAddress getInstanceAddress();

    /**
     * getInstanceToken
     * Obtain our running Instance Token.
     * @return String representing our Local Server-Side Token.
     */
    String getInstanceToken();

}
