package your.microservice.idp.jwt;

import java.util.Map;

/**
 * YourMicroserviceManifest
 *
 * @author jeff.a.schenk@gmail.com on 3/3/16.
 */
public class YourMicroserviceManifest {
    /**
     * Your Microservice Manifest Identifier
     */
    private String id;
    /**
     * Manifest Data.
     */
    private Map<String, Object> data;

    /**
     * Default Constructor
     */
    public YourMicroserviceManifest() {
    }

    /**
     * YourMicroserviceManifest
     *
     * @param id   Identifier of Manifest
     * @param data Manifest Contents
     */
    public YourMicroserviceManifest(String id, Map<String, Object> data) {
        this.id = id;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
