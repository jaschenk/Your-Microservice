package your.microservice.example.controllers.rest;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import your.microservice.example.AppInfo;

/**
 * APIController
 *
 * @author jeff.a.schenk@gmail.com
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/example/{version}")
@SuppressWarnings("PMD")
public class ExampleAPPInfoController {
    /**
     * Common Logger
     */
    protected final static org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(ExampleAPPInfoController.class);

    /**
     * Constants
     */
    public static final String APPLICATION_JSON_WITH_UTF8_ENCODING_VALUE = "application/json;charset=UTF-8";

    /**
     * Application Info
     */
    @Qualifier("exampleAppInfo")
    @Autowired
    private AppInfo appInfo;

    /**
     * getApiInfo
     * Obtain the current Service Application Information.
     *
     * @param version Optional Version of API
     * @return Environments Containing Supported Environments.
     */
    @RequestMapping(
            method = RequestMethod.GET,
            produces = APPLICATION_JSON_WITH_UTF8_ENCODING_VALUE
    )
    @ResponseBody
    public AppInfo getApiInfo(@PathVariable String version) {
        return appInfo;
    }

}
