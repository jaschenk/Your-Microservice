package your.microservice.core.security.idp.model.types;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created with IntelliJ IDEA.
 * User: jaschenk
 * Date: 1/22/17
 * Time: 11:17 AM
 * To change
 * this template use File | Settings | File Templates.
 */
@Converter
public class YourEntityStatusConverter implements AttributeConverter<YourEntityStatus, String> {

        @Override
        public String convertToDatabaseColumn(YourEntityStatus attribute) {
                return attribute.name();
        }

        @Override
        public YourEntityStatus convertToEntityAttribute(String dbData) {
           return YourEntityStatus.getTypeByName(dbData);
        }

}
