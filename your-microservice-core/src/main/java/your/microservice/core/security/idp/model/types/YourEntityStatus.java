package your.microservice.core.security.idp.model.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * YourEntityStatus
 *
 * @author jeff.a.schenk@gmail.com
 */
public enum YourEntityStatus {

    INACTIVE (0),
    ACTIVE (1),
    PENDING (2),
    SUSPENDED(3),
    REMOVED(4),
    UNKNOWN (9);

    private final int iVal;
    YourEntityStatus(int iVal)
    {
        this.iVal = iVal;
    }

    /**
     * Get the YourEntityTokenStatus by Name
     *
     * @param typeName to be used to lookup up by String Name of Type.
     * @return AccountStatus
     */
    public static YourEntityStatus getTypeByName(String typeName) {
        if (StringUtils.isEmpty(typeName)) {
            return UNKNOWN;
        }
        for (YourEntityStatus element : YourEntityStatus.values()) {
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
        for (YourEntityStatus element : YourEntityStatus.values()) {
            if (element.equals(YourEntityStatus.UNKNOWN)) {
                continue;
            }
            statusValues.add(element.toString());
        }
        return statusValues;
    }

}
