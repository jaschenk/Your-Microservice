package your.microservice.core.dm.dto.system;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * YourPulse
 * Provides the DTO for the System YourPulse representation.
 *
 * @author jeff.a.schenk@gmail.com on 9/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class YourPulse implements Serializable {
    /**
     * YourPulse UUID
     */
    private String pulseUUID;
    /**
     * Current Cloud Time of Cloud Instance in Milliseconds
     */
    private Long cloudTime;
    /**
     * Current Cloud Status
     */
    private String cloudStatus;
    /**
     * Current Cloud Version Information
     */
    private String cloudVersion;
    /**
     * Current Requests Instance Target
     */
    private String cloudInstance;
    /**
     * Additional Payload
     */
    private Object additionalInformation;

    /**
     * Default Constructor
     */
    public YourPulse() {
        this.additionalInformation = null;
        this.pulseUUID = UUID.randomUUID().toString();
        this.cloudTime = Instant.now().toEpochMilli();
    }

    public String getPulseUUID() {
        return pulseUUID;
    }
    public Long getCloudTime() {
        return cloudTime;
    }

    public String getCloudStatus() {
        return cloudStatus;
    }

    public void setCloudStatus(String cloudStatus) {
        this.cloudStatus = cloudStatus;
    }

    public String getCloudVersion() {
        return cloudVersion;
    }

    public void setCloudVersion(String cloudVersion) {
        this.cloudVersion = cloudVersion;
    }

    public String getCloudInstance() {
        return cloudInstance;
    }

    public void setCloudInstance(String cloudInstance) {
        this.cloudInstance = cloudInstance;
    }

    public Object getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(Object additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    @Override
    public String toString() {
        return "YourPulse{" +
                "pulseUUID='" + pulseUUID + '\'' +
                ", cloudTime=" + cloudTime +
                ", cloudStatus='" + cloudStatus + '\'' +
                ", cloudVersion='" + cloudVersion + '\'' +
                ", cloudInstance='" + cloudInstance + '\'' +
                ", additionalInformation=" + additionalInformation +
                '}';
    }
}
