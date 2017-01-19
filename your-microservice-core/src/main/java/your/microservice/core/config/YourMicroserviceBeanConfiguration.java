package your.microservice.core.config;

import your.microservice.core.annotation.EnableYourMicroservice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

/**
 * YourMicroserviceBeanConfiguration
 * <p>
 * Enables wiring in all applicable configurations
 * to Enable Common Your Microservice Facilities
 * for the Service using this facility.
 *
 * @author jeff.a.schenk@gmail.com on 2/21/16.
 */
public class YourMicroserviceBeanConfiguration implements ImportSelector,
        ImportAware, EnvironmentAware, BeanFactoryAware {
    /**
     * Logging
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(YourMicroserviceBeanConfiguration.class);
    /**
     * Save for Import Metadata.
     */
    private AnnotationAttributes enableYourMicroservice;

    /**
     * Environment
     */
    @SuppressWarnings("PMD")
    private Environment environment;

    /**
     * Bean Factory.
     */
    @SuppressWarnings("PMD")
    private BeanFactory beanFactory;

    /**
     * setImportMetadata
     *
     * @param importMetadata Annotations Metadata to validate...
     */
    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        Map<String, Object> map = importMetadata.getAnnotationAttributes(EnableYourMicroservice.class.getName());
        this.enableYourMicroservice = AnnotationAttributes.fromMap(map);
        if (this.enableYourMicroservice == null) {
            String message =
                    "@EnableYourMicroservice is not present on importing class " + importMetadata.getClassName();
            LOGGER.error(message);
            throw new IllegalArgumentException(message);
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * selectImports
     * <p>
     * Provides a configuration list of additional Import which should be performed to
     * implement the applicable configuration.
     *
     * @param importingClassMetadata Annotations Metadata to use to construct Imports.
     * @return String Array of Configuration Imports.
     */
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        AnnotationAttributes attributes =
                AnnotationAttributes.fromMap(
                        importingClassMetadata.getAnnotationAttributes(EnableYourMicroservice.class.getName(), false));
        String environmentType = attributes.getString("environmentType");
        LOGGER.info("Using specified EnvironmentType:[{}]", environmentType);
        /**
         *  Create our necessary Imports.
         */
        return new String[]{
                YourMicroserviceEnvironmentConfiguration.class.getName()

                // Add Security Import as Applicable ...
        };

    }



}
