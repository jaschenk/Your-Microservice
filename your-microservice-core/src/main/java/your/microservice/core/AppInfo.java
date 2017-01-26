package your.microservice.core;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * MicroServices Base Application Information
 */
@Component("coreAppInfo")
@PropertySource({"classpath:core.build.properties"})
public class AppInfo {

    @Value("${build.version}")
    private String buildVersion;

    @Value("${build.date}")
    private String buildTimestamp;

    @Value("${build.name}")
    private String buildName;

    @Value("${build.description}")
    private String buildDescription;

    @Value("${build.artifactId}")
    private String buildArtifactId;

    @Value("${build.groupId}")
    private String buildGroupId;

    public String getBuildVersion() {
        return buildVersion;
    }

    public String getBuildTimestamp() {
        return buildTimestamp;
    }

    public String getBuildName() {
        return buildName;
    }

    public String getBuildDescription() {
        return buildDescription;
    }

    public String getBuildArtifactId() {
        return buildArtifactId;
    }

    public String getBuildGroupId() {
        return buildGroupId;
    }
}
