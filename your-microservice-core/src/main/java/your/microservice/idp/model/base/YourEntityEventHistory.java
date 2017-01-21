package your.microservice.idp.model.base;

import com.fasterxml.jackson.annotation.JsonBackReference;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Date;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * YourEntityEventHistory
 *
 * @author jeff.a.schenk@gmail.com on 11/9/15.
 */
@Entity
public class YourEntityEventHistory implements Serializable {

    /**
     * Constants for Headers to obtain true Requester,
     * http://docs.aws.amazon.com/ElasticLoadBalancing/latest/DeveloperGuide/x-forwarded-headers.html
     *
     * X-Forwarded-For
     * ---------------
     * The X-Forwarded-For request header helps you identify the IP address of a client when you use an HTTP or HTTPS load balancer. Because load balancers intercept traffic between clients and servers, your server access logs contain only the IP address of the load balancer. To see the IP address of the client, use the X-Forwarded-For request header. Elastic Load Balancing stores the IP address of the client in the X-Forwarded-For request header and passes the header to your server.
     *
     * The X-Forwarded-For request header takes the following form:
     *
     * X-Forwarded-For: clientIPAddress
     * The following is an example X-Forwarded-For request header for a client with an IP address of 203.0.113.7.
     *
     * X-Forwarded-For: 203.0.113.7
     * The following is an example X-Forwarded-For request header for a client with an IPv6 address of 2001:DB8::21f:5bff:febf:ce22:8a2e.
     *
     * X-Forwarded-For: 2001:DB8::21f:5bff:febf:ce22:8a2e
     * If a request goes through multiple proxies, the clientIPAddress in the X-Forwarded-For request header is followed by the IP addresses of each successive proxy that the request goes through before it reaches your load balancer. Therefore, the right-most IP address is the IP address of the most recent proxy and the left-most IP address is the IP address of the originating client. In such cases, the X-Forwarded-For request header takes the following form:
     *
     * X-Forwarded-For: OriginatingClientIPAddress, proxy1-IPAddress, proxy2-IPAddress
     *
     *
     * X-Forwarded-Proto
     * -----------------
     * The X-Forwarded-Proto request header helps you identify the protocol (HTTP or HTTPS) that
     * a client used to connect to your server. Your server access logs contain only the protocol used between the server and the load balancer; they contain no information about the protocol used between the client and the load balancer. To determine the protocol used between the client and the load balancer, use the X-Forwarded-Proto request header. Elastic Load Balancing stores the protocol used between the client and the load balancer in the X-Forwarded-Proto request header and passes the header along to your server.
     *
     * Your application or website can use the protocol stored in the X-Forwarded-Proto request header to render a response that redirects to the appropriate URL.
     *
     * The X-Forwarded-Proto request header takes the following form:
     *
     * X-Forwarded-Proto: originatingProtocol
     * The following example contains an X-Forwarded-Proto request header for a request that originated from the client as an HTTPS request:
     *
     * X-Forwarded-Proto: https
     *
     *
     * X-Forwarded-Port
     * ----------------
     * The X-Forwarded-Port request header helps you identify the port that an HTTP or HTTPS load balancer uses to connect to the client.
     */
    public static final String PROPERTY_TAG_NAME_XFORWARDED_FOR = "X-Forwarded-For";
    public static final String PROPERTY_TAG_NAME_XFORWARDED_PROTO = "X-Forwarded-Proto";
    public static final String PROPERTY_TAG_NAME_XFORWARDED_PORT = "X-Forwarded-Port";
    /**
     * Additional Headers
     */
    public static final String PROPERTY_TAG_NAME_USER_AGENT = "user-agent";
    public static final String PROPERTY_TAG_NAME_HOST = "host";

    /**
     * Identifier
     */
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    /**
     * Defined Scope of this Event to Entity.
     */
    @JsonBackReference
    @OneToOne(optional=false)
    @JoinColumn(name = "entityId")
    private YourEntity yourEntity;

    /**
     * Event Tag Name.
     */
    @NotNull
    @Column(name = "eventtagname", nullable = false, length = 64)
    private String eventTagName;

    /**
     * Event Tag Properties.
     */
    @ElementCollection
    @MapKeyColumn(name="name")
    @Column(name="value")
    @CollectionTable(name="EventTagProperties", joinColumns=@JoinColumn(name="eventTagProperties_id"))
    private Map<String, String> eventTagProperties = new HashMap<>();

    /**
     * Event Message
     */
    @NotNull
    @Column(name = "eventmessage", nullable = true, length = 1024)
    private String eventMessage;

    @NotNull
    @Column(name = "createdbydate", nullable = false)
    private java.util.Date createdByDate;

    /**
     * YourEntityEventHistory
     *
     * @param yourEntity -- Reference of Entity which has just performed the Event.
     * @param eventTagName       -- Event Tag Name
     * @param eventMessage       -- Event Message Text
     * @param eventTagProperties -- Event Properties
     */
    public YourEntityEventHistory(YourEntity yourEntity, String eventTagName, String eventMessage, Map<String, String> eventTagProperties) {
        super();
        this.setCreatedByDate(Date.from(Instant.now()));
        this.yourEntity = yourEntity;
        this.eventTagName = eventTagName;
        this.eventMessage = eventMessage;
        this.eventTagProperties = eventTagProperties;
    }

    /**
     * YourEntityEventHistory
     *
     * @param eventTagName -- Event Tag Name
     * @param eventMessage -- Event Message Text
     * @param eventTagProperties -- Event Properties
     */
    public YourEntityEventHistory(String eventTagName, String eventMessage, Map<String, String> eventTagProperties) {
        super();
        this.setCreatedByDate(Date.from(Instant.now()));
        this.eventTagName = eventTagName;
        this.eventMessage = eventMessage;
        this.eventTagProperties = eventTagProperties;
    }

    /**
     * Default No Argument Constructor
     */
    public YourEntityEventHistory() {
    }

    public YourEntity getYourEntity() {
        return yourEntity;
    }

    public void setYourEntity(YourEntity yourEntity) {
        this.yourEntity = yourEntity;
    }

    public String getEventTagName() {
        return eventTagName;
    }

    public void setEventTagName(String eventTagName) {
        this.eventTagName = eventTagName;
    }

    public String getEventMessage() {
        return eventMessage;
    }

    public void setEventMessage(String eventMessage) {
        this.eventMessage = eventMessage;
    }

    public Map<String, String> getEventTagProperties() {
        return eventTagProperties;
    }

    public void setEventTagProperties(Map<String, String> eventTagProperties) {
        this.eventTagProperties = eventTagProperties;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public java.util.Date getCreatedByDate() {
        return createdByDate;
    }

    public void setCreatedByDate(java.util.Date createdByDate) {
        this.createdByDate = createdByDate;
    }

    /**
     * Entity Event Tag Names
     */
    public enum EntityEventTagNames implements Serializable {
        LAST_LOGIN(1),
        LAST_PASSWORD_CHANGE (2),
        LAST_ACTIVE_ORG (3),
        LAST_FLIGHT_CREATED (4),
        LAST_FLIGHT_UPDATED (5),
        LAST_FLIGHT_LOGGED (6),
        LAST_FLIGHT_AREA_CREATED (7),
        LAST_FLIGHT_AREA_UPDATED (8),
        ACCEPTED_TERMS_OF_SERVICE (10),
        COMPLETED_INTRO (11),
        ORGANIZATION_ACCOUNT_PLAN_SUBSCRIPTION (12),
        ORGANIZATION_ACCOUNT_PAYMENT_INFORMATION_UPDATED (13),
        LAST_TOKEN_REFRESH(97),
        LAST_LOGOUT(98),
        UNKNOWN(99);

        private final int iVal;

        EntityEventTagNames(int iVal) {
            this.iVal = iVal;
        }

        /**
         * Get the EntityEventTagNames by Name
         *
         * @param typeName to be used to lookup up by String Name of Type.
         * @return EntityEventTagNames
         */
        public static EntityEventTagNames getTypeByName(String typeName) {
            if (StringUtils.isEmpty(typeName)) {
                return UNKNOWN;
            }
            for (EntityEventTagNames element : EntityEventTagNames.values()) {
                if (element.toString().equalsIgnoreCase(typeName)) {
                    return element;
                }
            }
            return UNKNOWN;
        }

    }


    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
