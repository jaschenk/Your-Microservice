package your.microservice.idp.model.base;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Date;


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
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

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


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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


    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }


}
