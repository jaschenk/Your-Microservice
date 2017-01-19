package your.microservice;


/**
 * MicroServices Base Application Information
 */
public class AppInfo {

    private static final String description = "Your Commons MicroServices";

    private static final String version = "${project.version}";  //NOPMD

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }

}
