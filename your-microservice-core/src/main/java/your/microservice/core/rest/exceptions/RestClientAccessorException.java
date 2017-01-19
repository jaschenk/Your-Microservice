package your.microservice.core.rest.exceptions;

/**
 * RestClientAccessorException
 *
 * @author jeff.a.schenk@gmail.com on 2/12/16.
 */
public class RestClientAccessorException extends RuntimeException {
    /**
     * HTTP Response Code.
     */
    private int responseCode;
    /**
     * Resource Entity for Request.
     */
    private String entity;

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     * @param responseCode HTTP Response Code.
     */
    public RestClientAccessorException(String message, int responseCode) {
        super(message);
        this.responseCode = responseCode;
    }

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     * @param entity Principal of Entity Attempting Authentication.
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     * @param responseCode HTTP Response Code.
     */
    public RestClientAccessorException(String entity, String message, int responseCode) {
        super(message);
        this.entity = entity;
        this.responseCode = responseCode;
    }

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param entity Principal of Entity Attempting Authentication.
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param responseCode HTTP Response Code.
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A <tt>null</tt> value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     * @since 1.4
     */
    public RestClientAccessorException(String entity, String message, int responseCode, Throwable cause) {
        super(message, cause);
        this.responseCode = responseCode;
        this.entity = entity;
    }

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param responseCode HTTP Response Code.
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A <tt>null</tt> value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     * @since 1.4
     */
    public RestClientAccessorException(String message, int responseCode, Throwable cause) {
        super(message, cause);
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getEntity() {
        return entity;
    }
}
