package your.microservice.core.controllers.system;

import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import your.microservice.AppInfo;

import javax.servlet.http.HttpSession;

/**
 * AppInfoRESTController
 *
 * @author jeff.a.schenk@gmail.com 04/07/2016.
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
     * Constants
     */
    public static final String APPLICATION_JSON_WITH_UTF8_ENCODING_VALUE = "application/json;charset=UTF-8";

    /**
     * getApiInfo
     * Obtain the current Service Application Information.
     *
     * @param version Optional Version of API
     * @param httpSession Reference
     * @return Environments Containing Supported Environments.
     */
    @RequestMapping(
            method = RequestMethod.GET,
            produces = APPLICATION_JSON_WITH_UTF8_ENCODING_VALUE
    )
    @ResponseBody
    public AppInfo getApiInfo(@PathVariable String serviceName, @PathVariable String version, HttpSession httpSession) {
        return new AppInfo();
    }

}
