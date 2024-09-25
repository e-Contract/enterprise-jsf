/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.integ.be.e_contract.ejsf;

import be.e_contract.ejsf.component.clocksync.ClockSyncErrorEvent;
import be.e_contract.ejsf.component.clocksync.ClockSyncEvent;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.integ.be.e_contract.ejsf.cdi.TestScoped;

@Named
@TestScoped
public class ClockSyncController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClockSyncController.class);

    private Long bestRoundTripDelay;

    private Long deltaT;

    private boolean synced;

    private boolean error;

    @PostConstruct
    public void postConstruct() {
        this.synced = false;
        this.error = false;
        this.bestRoundTripDelay = null;
        this.deltaT = null;
    }

    public void clockSynchronized(ClockSyncEvent event) {
        this.bestRoundTripDelay = event.getBestRoundTripDelay();
        this.deltaT = event.getDeltaT();
        LOGGER.debug("best round trip delay: {} ms", this.bestRoundTripDelay);
        LOGGER.debug("delta T: {} ms", this.deltaT);
        this.synced = true;
    }

    public void clockSyncError(ClockSyncErrorEvent event) {
        LOGGER.debug("clock sync error: {}", event.getErrorMessage());
        this.error = true;
    }

    public Long getBestRoundTripDelay() {
        return this.bestRoundTripDelay;
    }

    public Long getDeltaT() {
        return this.deltaT;
    }

    public boolean isSynced() {
        return this.synced;
    }

    public boolean isError() {
        return this.error;
    }
}
