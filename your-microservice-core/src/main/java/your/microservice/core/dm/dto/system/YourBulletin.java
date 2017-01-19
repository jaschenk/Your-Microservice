package your.microservice.core.dm.dto.system;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import your.microservice.core.dm.serialization.JsonDateSerializer;

import java.io.Serializable;
import java.util.Date;

/**
 * YourBulletin
 *
 * @author jeff.a.schenk@gmail.com on 8/28/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class YourBulletin implements Serializable {
    @JsonIgnore
    public static final String BULLETIN_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    /**
     * Message Level
     */
    private String messageLevel;
    /**
     * Message Text
     */
    private String message;
    /**
     * Message Type
     */
    private String messageType;
    /**
     * Current Cloud Status
     */
    private String cloudStatus;
    /**
     * Current Requests Instance Target
     */
    private String cloudInstance;
    /**
     * Current Cloud Version Information
     */
    private String cloudVersion;
    /**
     * Additional Informational URL, to allow a link
     * regarding Certain System Events which dictate
     * additional communication to end consumer
     * for this Bulletin System Event.
     */
    private String moreInfoUrl;
    /**
     * Future Time When Event will Occur
     */
    @JsonSerialize(using = JsonDateSerializer.class)
    private Date messageFutureEventTime;

    /**
     * Default non-parameter Constructor.
     */
    public YourBulletin() {
    }

    public String getMessageLevel() {
        return messageLevel;
    }

    public void setMessageLevel(String messageLevel) {
        this.messageLevel = messageLevel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getCloudStatus() {
        return cloudStatus;
    }

    public void setCloudStatus(String cloudStatus) {
        this.cloudStatus = cloudStatus;
    }

    public String getCloudInstance() {
        return cloudInstance;
    }

    public void setCloudInstance(String cloudInstance) {
        this.cloudInstance = cloudInstance;
    }

    public String getCloudVersion() {
        return cloudVersion;
    }

    public void setCloudVersion(String cloudVersion) {
        this.cloudVersion = cloudVersion;
    }

    public Date getMessageFutureEventTime() {
        return messageFutureEventTime;
    }

    public void setMessageFutureEventTime(Date messageFutureEventTime) {
        this.messageFutureEventTime = messageFutureEventTime;
    }

    public String getMoreInfoUrl() {
        return moreInfoUrl;
    }

    public void setMoreInfoUrl(String moreInfoUrl) {
        this.moreInfoUrl = moreInfoUrl;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("YourBulletin{");
        sb.append("messageLevel='" + messageLevel + '\'');
        sb.append(", message='" + message + '\'');
        sb.append(", messageType='" + messageType + '\'');
        sb.append(", cloudStatus='" + cloudStatus + '\'');
        sb.append(", cloudInstance='" + cloudInstance + '\'');
        sb.append(", cloudVersion='" + cloudVersion + '\'');
        if (moreInfoUrl != null) {
            sb.append(", moreInfoUrl='" + moreInfoUrl + '\'');
        }
        if (messageFutureEventTime != null) {
            sb.append(", messageFutureEventTime=" + messageFutureEventTime);
        }
        sb.append('}');
        return sb.toString();
    }

    /**
     * Cloud Status Types.
     */
    public enum CloudStatusType {
        OK,
        WAITING_TO_SHUTDOWN
    }

    /**
     * Message Types.
     */
    public enum MessageType {
        CLOUD_BULLETIN
    }

    /**
     * Message Levels.
     */
    public enum MessageLevel {
        INFO,
        WARN,
        URGENT
    }
}
