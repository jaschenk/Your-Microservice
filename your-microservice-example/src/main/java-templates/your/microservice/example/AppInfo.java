package your.microservice.example;

/**
 * Your Microservice Base Application Information
 */
public class AppInfo {

    private static final String description = "Your Microservice -- Template/Example";

    private static final String version = "${project.version}"; //NOPMD

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }

    public String getFrontEndVersion() { return ""; }

}
