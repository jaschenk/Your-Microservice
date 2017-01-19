package your.microservice.core.rest.exceptions;

/**
 * NotAuthenticatedException
 *
 * @author jeff.a.schenk@gmail.com on 2/12/16.
 */
public class NotAuthenticatedException extends RuntimeException {
    /**
     * HTTP Response Code.
     */
    private int responseCode;
    /**
     * Principal Used for Request.
     */
    private String principal;

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     * @param principal Principal of Entity Attempting Authentication.
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     * @param responseCode HTTP Response Code.
     */
    public NotAuthenticatedException(String principal, String message, int responseCode) {
        super(message);
        this.principal = principal;
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getPrincipal() {
        return principal;
    }
}
