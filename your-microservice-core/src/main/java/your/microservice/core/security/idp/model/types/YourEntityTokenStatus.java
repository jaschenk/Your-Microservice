package your.microservice.core.security.idp.model.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * YourEntityTokenStatus
 *
 * @author jeff.a.schenk@gmail.com on 3/30/16.
 */
public enum YourEntityTokenStatus {

    INACTIVE (0),
    ACTIVE (1),
    PENDING (2),
    REVOKED(3),
    UNKNOWN (9);

    private final int iVal;
    YourEntityTokenStatus(int iVal)
    {
        this.iVal = iVal;
    }

    /**
     * Get the YourEntityTokenStatus by Name
     *
     * @param typeName to be used to lookup up by String Name of Type.
     * @return AccountStatus
     */
    public static YourEntityTokenStatus getTypeByName(String typeName) {
        if (StringUtils.isEmpty(typeName)) {
            return UNKNOWN;
        }
        for (YourEntityTokenStatus element : YourEntityTokenStatus.values()) {
            if (element.toString().equalsIgnoreCase(typeName) ||
                    element.toString().equalsIgnoreCase(typeName.replace(' ','_'))) {
                return element;
            }
        }
        return UNKNOWN;
    }

    /**
     * Obtain a List of Entity Status List
     * @return List containing Strings which represent Token Status
     */
    @JsonIgnore
    public static List<String> getEntityStatusList() {
        List<String> statusValues = new ArrayList<>();
        for (YourEntityTokenStatus element : YourEntityTokenStatus.values()) {
            if (element.equals(YourEntityTokenStatus.UNKNOWN)) {
                continue;
            }
            statusValues.add(element.toString());
        }
        return statusValues;
    }

}
