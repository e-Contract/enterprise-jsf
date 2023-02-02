/*
 * Enterprise JSF project.
 *
 * Copyright 2021-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.clocksync;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import org.primefaces.event.AbstractAjaxBehaviorEvent;

public class ClockSyncEvent extends AbstractAjaxBehaviorEvent {

    public static final String NAME = "sync";

    private final long bestRoundTripDelay;

    private final long deltaT;

    public ClockSyncEvent(UIComponent component, Behavior behavior, long bestRoundTripDelay, long deltaT) {
        super(component, behavior);
        this.bestRoundTripDelay = bestRoundTripDelay;
        this.deltaT = deltaT;
    }

    public long getBestRoundTripDelay() {
        return this.bestRoundTripDelay;
    }

    public long getDeltaT() {
        return this.deltaT;
    }
}
