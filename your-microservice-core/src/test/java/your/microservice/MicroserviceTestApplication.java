package your.microservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import your.microservice.core.annotation.EnableYourMicroservice;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableYourMicroservice
@ComponentScan(basePackages = {"your.microservice.testutil"})
public class MicroserviceTestApplication {

    /**
     * Logging
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(MicroserviceTestApplication.class);

    /**
     * MicroServices Application BootStrap
     *
     * @param args Incoming Program Arguments Array.
     */
    public static void main(String[] args) {
        /**
         * Bootstrap the MicroServices Stack.
         */
        SpringApplication.run(MicroserviceTestApplication.class, args);
    }

    @PostConstruct
    void applicationPostConstruction() {
        LOGGER.info("Test MicroService has been Successfully Bootstrapped.");
    }

}
