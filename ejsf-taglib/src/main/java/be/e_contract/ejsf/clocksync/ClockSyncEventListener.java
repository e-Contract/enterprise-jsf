/*
 * Enterprise JSF project.
 *
 * Copyright 2021-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.clocksync;

import javax.faces.event.FacesListener;

public interface ClockSyncEventListener extends FacesListener {

    void processEvent(ClockSyncEvent clockSyncEvent);
}
