package your.microservice.core.configuration;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;
import org.springframework.context.annotation.Configuration;

/**
 * YourMicroserviceMetricsConfiguration
 *
 * @author jeff.a.schenk@gmail.com
 */
@Configuration
@EnableMetrics
public class MetricsConfig extends MetricsConfigurerAdapter {

    /**
     * Configure our Reporters
     * @param metricRegistry Our Metric Registry
     */
    @Override
    public void configureReporters(MetricRegistry metricRegistry) {
        // registerReporter allows the MetricsConfigurerAdapter to
        // shut down the reporter when the Spring context is closed
        JmxReporter.forRegistry(metricRegistry).build().start();
        /**
         * Logging to Log
        registerReporter(Slf4jReporter
                .forRegistry(metricRegistry)
                .build())
                .start(1, TimeUnit.MINUTES);
         **/
    }

}
