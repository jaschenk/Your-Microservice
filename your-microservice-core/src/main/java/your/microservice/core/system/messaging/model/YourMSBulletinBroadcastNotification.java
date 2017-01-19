package your.microservice.core.system.messaging.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import your.microservice.core.dm.dto.system.YourBulletin;

/**
 * YourMSBulletinBroadcastNotification
 *
 * @author jeff.a.schenk@gmail.com on 8/25/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class YourMSBulletinBroadcastNotification extends YourMSNotification {
    /**
     * Latest Your Microservice Bulletin
     */
    private YourBulletin yourBulletin;

    /**
     * Default Constructor for our Notification.
     */
    public YourMSBulletinBroadcastNotification() {
        super(NotificationScope.BULLETIN_BROADCAST);
    }

    /**
     * Default Constructor for our Notification, with
     * all Applicable parameters.
     *
     * @param yourBulletin Your Microservice Bulletin to Post in Notification.
     */
    public YourMSBulletinBroadcastNotification(YourBulletin yourBulletin) {
        super(NotificationScope.BULLETIN_BROADCAST);
        this.yourBulletin = yourBulletin;
    }

    public YourBulletin getYourBulletin() {
        return yourBulletin;
    }

    public void setYourBulletin(YourBulletin yourBulletin) {
        this.yourBulletin = yourBulletin;
    }
}
