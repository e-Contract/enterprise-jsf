/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.geolocation;

import javax.faces.event.FacesListener;

public interface GeolocationEventListener extends FacesListener {

    void processEvent(GeolocationEvent clockSyncEvent);
}
