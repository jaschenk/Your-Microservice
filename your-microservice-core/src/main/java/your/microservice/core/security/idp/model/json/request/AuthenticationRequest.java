package your.microservice.core.security.idp.model.json.request;

import java.io.Serializable;

/**
 * AuthenticationRequest
 * DTO for incoming Authentication Request.
 */
public class AuthenticationRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Needs to change to principal, aka user name, email address...
     */
    private String username;
    /**
     * Represents Credentials of Principal.
     * Incoming request, credentials once validate, must be destroyed so clear textual password is not
     * found in memory.
     */
    private String password;

    /**
     * Default Constructor
     */
    public AuthenticationRequest() {
        super();
    }

    /**
     * Default Constructor
     *
     * @param username -- Principal
     * @param password -- Credentials
     */
    public AuthenticationRequest(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
