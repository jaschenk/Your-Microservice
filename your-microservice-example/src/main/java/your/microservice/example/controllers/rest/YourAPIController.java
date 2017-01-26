package your.microservice.example.controllers.rest;

import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * APIController
 *
 * @author jeff.a.schenk@gmail.com on 7/1/15.
 */
@RestController
@RequestMapping("/api/example/{version}")
public class YourAPIController {
    /**
     * Common Logger
     */
    protected final static org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(YourAPIController.class);

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
    public AppInfo getApiInfo(@PathVariable String version, HttpSession httpSession) {
        return new AppInfo();
    }








}
