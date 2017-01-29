package your.microservice.core.system.metrics;

import com.codahale.metrics.*;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * LoggingMetricRegistryListener
 *
 * @author jeff.a.schenk@gmail.com
 */
@Component
public class LoggingMetricRegistryListener implements MetricRegistryListener {

    /**
     * Common Logger
     */
    protected final static org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(LoggingMetricRegistryListener.class);

    @Override
    public void onGaugeAdded(String s, Gauge<?> gauge) {
        LOGGER.info("Gauge added: {}", s);
    }

    @Override
    public void onGaugeRemoved(String s) {
        LOGGER.info("Gauge removed: {}", s);
    }

    @Override
    public void onCounterAdded(String s, Counter counter) {
        LOGGER.info("Counter added: {}", s);
    }

    @Override
    public void onCounterRemoved(String s) {
        LOGGER.info("Counter removed: {}", s);
    }

    @Override
    public void onHistogramAdded(String s, Histogram histogram) {
        LOGGER.info("Histogram added: {}", s);
    }

    @Override
    public void onHistogramRemoved(String s) {
        LOGGER.info("Histogram removed: {}", s);
    }

    @Override
    public void onMeterAdded(String s, Meter meter) {
        LOGGER.info("Meter added: {}", s);
    }

    @Override
    public void onMeterRemoved(String s) {
        LOGGER.info("Meter removed: {}", s);
    }

    @Override
    public void onTimerAdded(String s, Timer timer) {
        LOGGER.info("Timer added: {}", s);
    }

    @Override
    public void onTimerRemoved(String s) {
        LOGGER.info("Timer removed: {}", s);
    }
}
