package your.microservice.core.util;

import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * ResourceUtility
 *
 * Simple Static utility Class to Load A Specified
 * property value from the contents of a provide External File
 * or internal Resource.
 * @author jeff.a.schenk@gmail.com on 10/7/15.
 */
public class ResourceUtility {
    /**
     * Logger
     */
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ResourceUtility.class);

    /**
     * Constants
     */
    protected static final String UNKNOWN = "UNKNOWN";

    /**
     * getValueFromResource
     *
     * @param resourceName Name of Resource, Example: resources/version.txt
     * @return String value containing contents of Resource Value or Default if Applicable.
     */
    public static String getValueFromResource(String resourceName) {
            return getValueFromResource(resourceName, UNKNOWN);
    }

    /**
     * getValueFromResource
     *
     * @param resourceName Name of Resource, Example: resources/version.txt
     * @param defaultValue Default Value if Resource Not Found.
     * @return String value containing contents of Resource Value or Default if Applicable.
     */
    public static String getValueFromResource(String resourceName, String defaultValue) {
        InputStream in = null;
        try {
            final ClassLoader applicationClassLoader = ResourceUtility.class.getClassLoader();
            in = applicationClassLoader.getResourceAsStream(resourceName);
            String value =
                    (in==null)?null:IOUtils.toString(in, "UTF-8");
            if (value == null) {
                LOGGER.error("Unable to Read Resource:[" + resourceName + "]");
                return defaultValue;
            } else {
                return value;
            }
        } catch (IOException ioe) {
            LOGGER.error("Exception Reading in Resource:[" + resourceName + "]");
            return defaultValue;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioe) {
                    LOGGER.error("IO Exception Finalizing the Resource Input Stream: " + ioe.getMessage(), ioe);
                }
            }
        }
    }

}
