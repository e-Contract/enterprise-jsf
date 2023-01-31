/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import be.e_contract.ejsf.clocksync.ClockSyncEvent;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("clockSyncController")
public class ClockSyncController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClockSyncController.class);

    public void clockSynchronized(ClockSyncEvent event) {
        LOGGER.debug("best round trip delay: {} ms", event.getBestRoundTripDelay());
        LOGGER.debug("delta T: {} ms", event.getDeltaT());
    }
}
