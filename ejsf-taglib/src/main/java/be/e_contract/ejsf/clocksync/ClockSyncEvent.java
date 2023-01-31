/*
 * Enterprise JSF project.
 *
 * Copyright 2021-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.clocksync;

import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;

public class ClockSyncEvent extends FacesEvent {

    private final long bestRoundTripDelay;

    private final long deltaT;

    public ClockSyncEvent(UIComponent component, long bestRoundTripDelay, long deltaT) {
        super(component);
        this.bestRoundTripDelay = bestRoundTripDelay;
        this.deltaT = deltaT;
    }

    @Override
    public boolean isAppropriateListener(FacesListener listener) {
        return (listener instanceof ClockSyncEventListener);
    }

    @Override
    public void processListener(FacesListener listener) {
        ClockSyncEventListener clockSyncEventListener = (ClockSyncEventListener) listener;
        clockSyncEventListener.processEvent(this);
    }

    public long getBestRoundTripDelay() {
        return this.bestRoundTripDelay;
    }

    public long getDeltaT() {
        return this.deltaT;
    }
}
