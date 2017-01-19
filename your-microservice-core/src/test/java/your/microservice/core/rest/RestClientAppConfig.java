package your.microservice.core.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * RestClientAppConfig
 *
 * @author jeff.a.schenk@gmail.com on 1/8/16.
 */
@Configuration
@ComponentScan(basePackages = {"your.microservice.core.rest"})
public class RestClientAppConfig {
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
