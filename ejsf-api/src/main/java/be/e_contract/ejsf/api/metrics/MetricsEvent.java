/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.api.metrics;

import java.util.LinkedList;
import java.util.List;

/**
 * CDI event fired to collect runtime metrics.
 *
 * @author Frank Cornelis
 */
public class MetricsEvent {

    private final List<Metric> metrics;

    public MetricsEvent() {
        this.metrics = new LinkedList<>();
    }

    public void addRuntimeMetric(String name, String value, String description) {
        Metric metric = new Metric(name, value, description);
        this.metrics.add(metric);
    }

    public List<Metric> getMetrics() {
        return this.metrics;
    }
}
