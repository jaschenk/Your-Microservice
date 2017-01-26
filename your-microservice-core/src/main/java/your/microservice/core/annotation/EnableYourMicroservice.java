package your.microservice.core.annotation;

import your.microservice.core.configuration.YourMicroserviceBeanConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * EnableYourMicroservice
 *
 * @author jeff.a.schenk@gmail.com on 2/21/16.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(YourMicroserviceBeanConfiguration.class)
public @interface EnableYourMicroservice {
    String environmentType() default "default";
}
