package your.microservice.idp.model.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;
import your.microservice.core.dm.serialization.JsonDateSerializer;
import your.microservice.idp.model.types.YourEntityTokenStatus;
import your.microservice.idp.model.types.YourEntityTokenStatusConverter;

import javax.persistence.*;
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
    @Column(name = "jti", unique = true, nullable = false, length = 64)
    private String jti;
    /**
     * Your Microservice Token Subject
     */
    @NotNull
    @Column(name = "subject", nullable = false, length = 256)
    private String subject;
    /**
     * Your Microservice Token Status
     */
    @NotNull
    @Column(name = "status", nullable = false, length = 32)
    @Convert(converter = YourEntityTokenStatusConverter.class)
    private YourEntityTokenStatus status;
    /**
     * Your Microservice Token Issued At Date Time.
     */
    @Column(name = "issuedat", nullable = false)
    @JsonSerialize(using=JsonDateSerializer.class)
    private Date issuedAt;
    /**
     * Your Microservice Token Expiration Date Time.
     */
    @Column(name = "expiration", nullable = false)
    @JsonSerialize(using=JsonDateSerializer.class)
    private Date expiration;
    /**
     * Your Microservice Token Not Used Before Date Time.
     */
    @Column(name = "notusedbefore", nullable = false)
    @JsonSerialize(using=JsonDateSerializer.class)
    private Date notUsedBefore;
    /**
     * Your Microservice Token Last Used Date Time.
     */
    @JsonSerialize(using=JsonDateSerializer.class)
    @Column(name = "lastused", nullable = false)
    private Date lastUsed;

    /**
     * Your Microservice Token Usage Count.
     */
    @Column(name = "usagecount", nullable = false)
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
