package your.microservice.core.dm.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 *
 */
public class WhiteSpaceTrimmer extends SimpleModule {

    public WhiteSpaceTrimmer() {
        addDeserializer(String.class, new StdScalarDeserializer<String>(String.class) {
            @Override
            public String deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
                    JsonProcessingException {
                return StringUtils.trim(jp.getValueAsString());
            }
        });
    }
}
