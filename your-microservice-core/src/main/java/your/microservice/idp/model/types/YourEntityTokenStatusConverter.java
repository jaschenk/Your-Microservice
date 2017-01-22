package your.microservice.idp.model.types;

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
public class YourEntityTokenStatusConverter implements AttributeConverter<YourEntityTokenStatus, String> {

        @Override
        public String convertToDatabaseColumn(YourEntityTokenStatus attribute) {
                return attribute.name();
        }

        @Override
        public YourEntityTokenStatus convertToEntityAttribute(String dbData) {
           return YourEntityTokenStatus.getTypeByName(dbData);
        }

}
