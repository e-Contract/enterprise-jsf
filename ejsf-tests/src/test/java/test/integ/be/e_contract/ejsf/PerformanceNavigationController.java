/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.integ.be.e_contract.ejsf;

import be.e_contract.ejsf.component.performance.PerformanceNavigationTimingEvent;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.integ.be.e_contract.ejsf.cdi.TestScoped;

@Named
@TestScoped
public class PerformanceNavigationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceNavigationController.class);

    private boolean done;

    @PostConstruct
    public void postConstruct() {
        this.done = false;
    }

    public void reset() {
        this.done = false;
    }

    public void handleEvent(PerformanceNavigationTimingEvent event) {
        LOGGER.debug("start time: {}", event.getStartTime());
        LOGGER.debug("duration: {}", event.getDuration());
        LOGGER.debug("request start: {}", event.getRequestStart());
        LOGGER.debug("response start: {}", event.getResponseStart());
        LOGGER.debug("response end: {}", event.getResponseEnd());
        LOGGER.debug("DOM interactive: {}", event.getDomInteractive());
        LOGGER.debug("DOM complete: {}", event.getDomComplete());
        LOGGER.debug("load event start: {}", event.getLoadEventStart());
        LOGGER.debug("load event end: {}", event.getLoadEventEnd());
        LOGGER.debug("name: {}", event.getName());
        this.done = true;
    }

    public boolean isDone() {
        return this.done;
    }
}
