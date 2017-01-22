package your.microservice.idp.model.base;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import your.microservice.idp.model.types.YourEntityStatus;
import your.microservice.idp.model.types.YourEntityStatusConverter;
import your.microservice.idp.model.types.YourEntityTokenStatus;
import your.microservice.idp.model.types.YourEntityTokenStatusConverter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Entity
public class YourEntityOrganization implements Serializable {

    /**
     * Standard Serialization Version Id.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Identifier
     */
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "entityOrgId", unique = true, nullable = false)
    private Long entityOrgId;

    @NotNull
    @Column(name = "name", unique = true, nullable = false, length = 64)
    private String name;

    @NotNull
    @Column(name = "createdbydate", nullable = false)
    private Date createdByDate;

    @NotNull
    @Column(name = "createdbyid", nullable = false, length = 128)
    private String createdByIdentifier;

    @NotNull
    @Column(name = "updatedbydate", nullable = false)
    private Date updatedByDate;

    @NotNull
    @Column(name = "updatedbyid", nullable = false, length = 128)
    private String updatedByIdentifier;

    /**
     * Your Entity Organization Status
     */
    @NotNull
    @Column(name = "status", nullable = false, length = 32)
    @Convert(converter = YourEntityStatusConverter.class)
    private YourEntityStatus status;

    /**
     * Entity Properties.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name="name")
    @Column(name="value")
    @CollectionTable(name="YourEntityOrgProperties",
            joinColumns=@JoinColumn(name="yourEntityOrgProperties_id"))
    private Map<String, String> entityOrgProperties = new HashMap<>();

    /**
     * Entities defined with Access to this Organization.
     */
    @ManyToMany(mappedBy = "yourEntityOrganizations", fetch = FetchType.EAGER)
    private Set<YourEntity> yourOrganizationEntities;

    /**
     * Default Constructor
     */
    public YourEntityOrganization() {}


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getEntityOrgId() {
        return entityOrgId;
    }

    public void setEntityOrgId(Long entityOrgId) {
        this.entityOrgId = entityOrgId;
    }

    public String getCreatedByIdentifier() {
        return createdByIdentifier;
    }

    public void setCreatedByIdentifier(String createdByIdentifier) {
        this.createdByIdentifier = createdByIdentifier;
    }

    public String getUpdatedByIdentifier() {
        return updatedByIdentifier;
    }

    public void setUpdatedByIdentifier(String updatedByIdentifier) {
        this.updatedByIdentifier = updatedByIdentifier;
    }

    public Date getCreatedByDate() {
        return createdByDate;
    }

    public void setCreatedByDate(Date createdByDate) {
        this.createdByDate = createdByDate;
    }

    public Date getUpdatedByDate() {
        return updatedByDate;
    }

    public void setUpdatedByDate(Date updatedByDate) {
        this.updatedByDate = updatedByDate;
    }

    public Map<String, String> getEntityOrgProperties() {
        return entityOrgProperties;
    }

    public void setEntityOrgProperties(Map<String, String> entityOrgProperties) {
        this.entityOrgProperties = entityOrgProperties;
    }

    public Set<YourEntity> getYourOrganizationEntities() {
        return yourOrganizationEntities;
    }

    public void setYourOrganizationEntities(Set<YourEntity> yourOrganizationEntities) {
        this.yourOrganizationEntities = yourOrganizationEntities;
    }

    public YourEntityStatus getStatus() {
        return status;
    }

    public void setStatus(YourEntityStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }


}
