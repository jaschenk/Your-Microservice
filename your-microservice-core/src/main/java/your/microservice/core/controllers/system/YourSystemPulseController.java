package your.microservice.core.controllers.system;

import org.springframework.security.core.Authentication;
import your.microservice.AppInfo;
import your.microservice.core.dm.dto.system.YourPulse;
import your.microservice.core.system.SystemInstanceStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * YourSystemPulseController
 *
 * Provides a method for the Front-End Cloud Application
 * to obtain a pulse on it's Back-End to which it is connected.
 *
 * @author jeff.a.schenk@gmail.com on 9/12/15.
 */
@RestController
@RequestMapping("/api/{serviceName}/{version}/system/pulse")
public class YourSystemPulseController {
    /**
     * Logging
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(YourSystemPulseController.class);
    /**
     * Static Application Information Object for this Runtime Instance.
     */
    private static final AppInfo appInfo = new AppInfo();
    /**
     * SystemInstanceStatusService Reference.
     */
    @Autowired
    private SystemInstanceStatusService systemInstanceStatusService;
    /**
     * Constants
     */
    public static final String APPLICATION_JSON_WITH_UTF8_ENCODING_VALUE = "application/json;charset=UTF-8";
    protected static final String UNKNOWN = "UNKNOWN";

    /**
     * getPulse
     * Obtain Pulse From Service Instance.
     *
     * @param authentication Reference to Authenticated Entity.
     * @param serviceName Service Name Associated to this Instance.
     * @param version API Version
     * @return Environments Containing Supported Environments.
     */
    @RequestMapping(
            value = {""},
            method = RequestMethod.GET,
            produces = APPLICATION_JSON_WITH_UTF8_ENCODING_VALUE
    )
    @ResponseBody
    public YourPulse getPulse(Authentication authentication,
                              @PathVariable String serviceName, @PathVariable String version) {
        /**
         * Initialize our Object to Return.
         */
        YourPulse pulse = new YourPulse();
        /**
         * Set the Front End and Cloud Instance Versions.
         */
        pulse.setCloudVersion(appInfo.getVersion());
        /**
         * Get Instance Address
         */
        pulse.setCloudInstance(
                systemInstanceStatusService.getInstanceToken() != null ?
                        systemInstanceStatusService.getInstanceToken() : UNKNOWN);
        /**
         * Set the Cloud Status.
         */
        pulse.setCloudStatus(systemInstanceStatusService.getCurrentSystemInstanceStatus());
        /**
         * Check Current Instance Status, if not OK,
         * include the Current Bulletin.
         */
        if (!systemInstanceStatusService.isCurrentSystemInstanceStatusOK()) {
            pulse.setAdditionalInformation(systemInstanceStatusService.getCurrentSystemInstanceStatusBulletin());
        }
        /**
         * Return Pulse DTO
         */
        LOGGER.info("{} {} Pulse sent to Consumer:[{}], {}",  serviceName, version, authentication.getName(), pulse);
        return pulse;
    }
}
