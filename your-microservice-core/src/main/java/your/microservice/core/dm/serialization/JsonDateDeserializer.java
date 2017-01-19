
package your.microservice.core.dm.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class JsonDateDeserializer extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser jsonparser,
            DeserializationContext deserializationcontext) throws IOException, JsonProcessingException  {

        String date = jsonparser.getText();
        try {
            return JsonDateSerializer.dateFormat.parse(date);
        } catch (java.text.ParseException ex) {
            Logger.getLogger(JsonDateDeserializer.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException("Parse Error", ex);
        }
    }
}
