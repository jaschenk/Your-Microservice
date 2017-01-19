package your.microservice.idp.model.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import your.microservice.core.dm.serialization.JsonDateSerializer;
import your.microservice.idp.model.types.YourEntityTokenStatus;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * YourEntityTokenHistory
 *
 * @author jeff.a.schenk@gmail.com on 7/25/16.
 */
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YourEntityTokenHistory implements Serializable {
    /**
     * Your Microservice Token Identifier
     */
    @Id
    private String jti;
    /**
     * Your Microservice Token Subject
     */
    @NotNull
    private String subject;
    /**
     * Your Microservice Token Status
     */
    @NotNull
    private YourEntityTokenStatus status;
    /**
     * Your Microservice Token Issued At Date Time.
     */
    @JsonSerialize(using=JsonDateSerializer.class)
    private Date issuedAt;
    /**
     * Your Microservice Token Expiration Date Time.
     */
    @JsonSerialize(using=JsonDateSerializer.class)
    private Date expiration;
    /**
     * Your Microservice Token Not Used Before Date Time.
     */
    @JsonSerialize(using=JsonDateSerializer.class)
    private Date notUsedBefore;
    /**
     * Your Microservice Token Last Used Date Time.
     */
    @JsonSerialize(using=JsonDateSerializer.class)
    private Date lastUsed;

    /**
     * Your Microservice Token Usage Count.
     */
    private Long usageCount;


    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public YourEntityTokenStatus getStatus() {
        return status;
    }

    public void setStatus(YourEntityTokenStatus status) {
        this.status = status;
    }

    public Date getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Date issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public Date getNotUsedBefore() {
        return notUsedBefore;
    }

    public void setNotUsedBefore(Date notUsedBefore) {
        this.notUsedBefore = notUsedBefore;
    }

    public Date getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Date lastUsed) {
        this.lastUsed = lastUsed;
    }

    public Long getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(Long usageCount) {
        this.usageCount = usageCount;
    }

    @Override
    public String toString() {
        return "YourEntityTokenHistory{" +
                "jti='" + jti + '\'' +
                ", subject='" + subject + '\'' +
                ", status=" + status +
                ", issuedAt=" + issuedAt +
                ", expiration=" + expiration +
                ", notUsedBefore=" + notUsedBefore +
                ", lastUsed=" + lastUsed +
                ", usageCount=" + usageCount +
                '}';
    }
}
