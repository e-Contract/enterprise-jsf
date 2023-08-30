/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.impl.metrics;

import be.e_contract.ejsf.api.metrics.Metric;
import be.e_contract.ejsf.api.metrics.MetricsEvent;
import java.util.List;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

@Named("ejsfMetricsController")
public class MetricsController {

    @Inject
    private Event<MetricsEvent> metricsEvent;

    public List<Metric> getRuntimeMetrics() {
        MetricsEvent metricsEvent = new MetricsEvent();
        this.metricsEvent.fire(metricsEvent);
        return metricsEvent.getMetrics();
    }
}
