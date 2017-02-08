package your.microservice.example;

import your.microservice.core.annotation.EnableYourMicroservice;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

/**
 * Spring Boot Application -- Your Microservice Enablement
 */
@SpringBootApplication
@EnableYourMicroservice
@ComponentScan(basePackages = {"your.microservice","your.microservice.example"})
public class Application {

    public static void main(String[] args) {
        /**
         * Perform the Spring Boot Application...
         */
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    void applicationPostConstruction() {
        LOGGER.info("Example Microservice Bootstrap Initialization...");
    }

    /**
     * Common Logger
     */
    protected final static org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(Application.class);

}
