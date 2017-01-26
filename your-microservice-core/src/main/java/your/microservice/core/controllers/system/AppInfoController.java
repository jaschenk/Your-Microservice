package your.microservice.core.controllers.system;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import your.microservice.core.AppInfo;

/**
 * AppInfoRESTController
 *
 * @author jeff.a.schenk@gmail.com
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/{serviceName}/{version}")
@SuppressWarnings("PMD")
public class AppInfoController {
    /**
     * Common Logger
     */
    protected final static org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(AppInfoController.class);

    /**
     * Application Info
     */
    @Qualifier("coreAppInfo")
    @Autowired
    private AppInfo appInfo;

    /**
     * Constants
     */
    public static final String APPLICATION_JSON_WITH_UTF8_ENCODING_VALUE = "application/json;charset=UTF-8";

    /**
     * getApiInfo
     * Obtain the current Service Application Information.
     *
     * @param authentication Reference
     * @param serviceName Service Name Called
     * @param version Optional Version of API
     * @return Environments Containing Supported Environments.
     */
    @RequestMapping(
            method = RequestMethod.GET,
            produces = APPLICATION_JSON_WITH_UTF8_ENCODING_VALUE
    )
    @ResponseBody
    public AppInfo getApiInfo(Authentication authentication, @PathVariable String serviceName, @PathVariable String version) {
        return appInfo;
    }

}
