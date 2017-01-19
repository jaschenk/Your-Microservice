package your.microservice.core.dm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 *
 */
public interface WrappedResult<E> extends Serializable {
    @JsonProperty
    E result();
}
