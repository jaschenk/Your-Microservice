package your.microservice.idp.model.base;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import your.microservice.idp.model.types.YourEntityStatus;
import your.microservice.idp.model.types.YourEntityStatusConverter;
import your.microservice.idp.model.types.YourEntityTokenStatus;
import your.microservice.idp.model.types.YourEntityTokenStatusConverter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.*;


@Entity
public class YourEntity implements Serializable {

    /**
     * Standard Serialization Version Id.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entityId", unique = true, nullable = false)
    private Long entityId;

    @NotNull
    @Column(name = "givenname", nullable = false, length = 64)
    private String entityGivenName;

    @NotNull
    @Column(name = "surname", nullable = false, length = 64)
    private String entitySurname;

    @NotNull
    @Column(name = "email", nullable = false, unique = true, length = 128)
    private String entityEmailAddress;

    @NotNull
    @Column(name = "credentials", nullable = false, length = 256)
    private String credentials;

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
    @CollectionTable(name="YourEntityProperties",
            joinColumns=@JoinColumn(name="yourEntityProperties_id"))
    private Map<String, String> entityProperties = new HashMap<>();

    /**
     * Entity Organizations
     */
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "yourentity_organizations",
            joinColumns = @JoinColumn(name = "yourentity_entityId",referencedColumnName = "entityId"),
            inverseJoinColumns = @JoinColumn(name = "yourentityorg_entityId", referencedColumnName = "entityOrgId"))
    private Set<YourEntityOrganization> yourEntityOrganizations;

    /**
     * Entity Roles
     */
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "yourentity_roles",
            joinColumns = @JoinColumn(name = "yourentity_entityId",referencedColumnName = "entityId"),
            inverseJoinColumns = @JoinColumn(name = "yourentityrole_entityId", referencedColumnName = "entityRoleId"))
    private Set<YourEntityRole> yourEntityRoles;

    /**
     * Your Entity Status
     */
    @NotNull
    @Column(name = "status", nullable = false, length = 32)
    @Convert(converter = YourEntityStatusConverter.class)
    private YourEntityStatus status;

    /**
     * Default Constructor
     */
    public YourEntity() {}

    public String getEntityGivenName() {
        return entityGivenName;
    }

    public void setEntityGivenName(String entityGivenName) {
        this.entityGivenName = entityGivenName;
    }

    public String getEntitySurname() {
        return entitySurname;
    }

    public void setEntitySurname(String entitySurname) {
        this.entitySurname = entitySurname;
    }

    public String getEntityEmailAddress() {
        return entityEmailAddress;
    }

    public void setEntityEmailAddress(String entityEmailAddress) {
        this.entityEmailAddress = entityEmailAddress;
    }

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }


    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
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

    public Map<String, String> getEntityProperties() {
        return entityProperties;
    }

    public void setEntityProperties(Map<String, String> entityProperties) {
        this.entityProperties = entityProperties;
    }

    public Set<YourEntityOrganization> getYourEntityOrganizations() {
        return yourEntityOrganizations;
    }

    public void setYourEntityOrganizations(Set<YourEntityOrganization> yourEntityOrganizations) {
        this.yourEntityOrganizations = yourEntityOrganizations;
    }

    public Set<YourEntityRole> getYourEntityRoles() {
        return yourEntityRoles;
    }

    public void setYourEntityRoles(Set<YourEntityRole> yourEntityRoles) {
        this.yourEntityRoles = yourEntityRoles;
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
