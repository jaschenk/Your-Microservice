package your.microservice.core.dm.dto.history;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import your.microservice.core.dm.serialization.JsonDateSerializer;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * EntityEventHistoryDTO
 *
 * @author jeff.a.schenk@gmail.com on 11/15/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class EntityEventHistoryDTO implements Serializable {
    /**
     * Event Created Data
     */
    @JsonSerialize(using=JsonDateSerializer.class)
    private Date createdByDate;
    /**
     * Event Message
     */
    private String eventMessage;
    /**
     * Event Tag Name.
     */
    private String eventTagName;

    /**
     * Event Tag Properties.
     */
    private Map<String, String> eventTagProperties = new HashMap<>();

    /**
     * EntityEventHistoryDTO
     *
     * @param createdByDate Event Date
     * @param eventMessage Event Message
     * @param eventTagName Tag Name
     * @param eventTagProperties Properties of Event
     */
    public EntityEventHistoryDTO(Date createdByDate, String eventMessage,
                                 String eventTagName, Map<String, String> eventTagProperties) {
        this.createdByDate = createdByDate;
        this.eventMessage = eventMessage;
        this.eventTagName = eventTagName;
        this.eventTagProperties = eventTagProperties;
    }

    public Date getCreatedByDate() {
        return createdByDate;
    }

    public void setCreatedByDate(Date createdByDate) {
        this.createdByDate = createdByDate;
    }

    public String getEventMessage() {
        return eventMessage;
    }

    public void setEventMessage(String eventMessage) {
        this.eventMessage = eventMessage;
    }

    public String getEventTagName() {
        return eventTagName;
    }

    public void setEventTagName(String eventTagName) {
        this.eventTagName = eventTagName;
    }

    public Map<String, String> getEventTagProperties() {
        return eventTagProperties;
    }

    public void setEventTagProperties(Map<String, String> eventTagProperties) {
        this.eventTagProperties = eventTagProperties;
    }
}
