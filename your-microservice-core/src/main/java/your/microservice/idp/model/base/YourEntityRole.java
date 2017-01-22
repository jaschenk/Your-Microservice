package your.microservice.idp.model.base;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import your.microservice.idp.model.types.YourEntityStatus;
import your.microservice.idp.model.types.YourEntityStatusConverter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Entity
public class YourEntityRole implements Serializable {

    /**
     * Standard Serialization Version Id.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Identifier
     */
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "entityRoleId", unique = true, nullable = false)
    private Long entityRoleId;

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
     * Entity Properties.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name="name")
    @Column(name="value")
    @CollectionTable(name="YourEntityRoleProperties",
            joinColumns=@JoinColumn(name="yourEntityRoleProperties_id"))
    private Map<String, String> entityRoleProperties = new HashMap<>();

    /**
     * Entities defined with Access to this Organization.
     */
    @ManyToMany(mappedBy = "yourEntityRoles")
    private Set<YourEntity> yourRoleEntities;

    /**
     * Your Entity Organization Status
     */
    @NotNull
    @Column(name = "status", nullable = false, length = 32)
    @Convert(converter = YourEntityStatusConverter.class)
    private YourEntityStatus status;


    /**
     * Default Constructor
     */
    public YourEntityRole() {}


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getEntityRoleId() {
        return entityRoleId;
    }

    public void setEntityRoleId(Long entityRoleId) {
        this.entityRoleId = entityRoleId;
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

    public Map<String, String> getEntityRoleProperties() {
        return entityRoleProperties;
    }

    public void setEntityRoleProperties(Map<String, String> entityRoleProperties) {
        this.entityRoleProperties = entityRoleProperties;
    }

    public Set<YourEntity> getYourRoleEntities() {
        return yourRoleEntities;
    }

    public void setYourRoleEntities(Set<YourEntity> yourRoleEntities) {
        this.yourRoleEntities = yourRoleEntities;
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
