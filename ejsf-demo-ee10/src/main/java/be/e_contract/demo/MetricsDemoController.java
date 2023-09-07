/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import be.e_contract.ejsf.api.metrics.MetricsEvent;
import java.time.LocalDateTime;
import jakarta.enterprise.event.Observes;

public class MetricsDemoController {

    public void addDemoMetric(@Observes MetricsEvent metricsEvent) {
        metricsEvent.addRuntimeMetric("test", "42", "This is a test metric.");
    }

    public void addTimeMetric(@Observes MetricsEvent metricsEvent) {
        metricsEvent.addRuntimeMetric("now", LocalDateTime.now().toString(), "The current time.");
    }
}
