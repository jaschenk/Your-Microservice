package your.microservice.core.system.messaging.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import your.microservice.core.dm.serialization.JsonDateSerializer;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * YourMSNotification
 *
 * @author jeff.a.schenk@gmail.com on 8/25/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public abstract class YourMSNotification implements Serializable {
    final static long serialVersionUID = 1L;
    /**
     * Default Notification Senders
     */
    static final String DEFAULT_NOTIFICATION_SENDER = "YOUR_MICROSERVICE_SYSTEM_INSTANCE";

    /**
     * Notification UUID
     */
    private String notificationUUID;
    /**
     * Time of Notification
     */
    @JsonSerialize(using=JsonDateSerializer.class)
    private Date notificationTime;
    /**
     * Scope of Notification
     */
    private NotificationScope notificationScope;
    /**
     * Notification Sender
     */
    private String notificationSender;

    /**
     * Default Constructor
     */
    YourMSNotification(NotificationScope notificationScope) {
        this.notificationUUID = UUID.randomUUID().toString();
        this.notificationTime = Date.from(Instant.now());
        this.notificationScope = notificationScope;
        this.notificationSender = DEFAULT_NOTIFICATION_SENDER;
    }

    public String getNotificationUUID() {
        return notificationUUID;
    }

    public Date getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(Date notificationTime) {
        this.notificationTime = notificationTime;
    }

    public String getNotificationSender() {
        return notificationSender;
    }

    public void setNotificationSender(String notificationSender) {
        this.notificationSender = notificationSender;
    }

    public NotificationScope getNotificationScope() {
        return notificationScope;
    }

    public void setNotificationScope(NotificationScope notificationScope) {
        this.notificationScope = notificationScope;
    }

    @Override
    public String toString() {
        return "YourMSNotification{" +
                "notificationUUID='" + notificationUUID + '\'' +
                ", notificationTime=" + notificationTime +
                ", notificationScope=" + notificationScope +
                ", notificationSender='" + notificationSender + '\'' +
                '}';
    }
}
