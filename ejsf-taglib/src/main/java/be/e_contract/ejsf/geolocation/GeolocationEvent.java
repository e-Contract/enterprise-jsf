/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.geolocation;

import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;

public class GeolocationEvent extends FacesEvent {

    private final double latitude;

    private final double longitude;

    private final double accuracy;

    public GeolocationEvent(UIComponent component, double latitude, double longitude, double accuracy) {
        super(component);
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
    }

    @Override
    public boolean isAppropriateListener(FacesListener listener) {
        return (listener instanceof GeolocationEventListener);
    }

    @Override
    public void processListener(FacesListener listener) {
        GeolocationEventListener geolocationEventListener = (GeolocationEventListener) listener;
        geolocationEventListener.processEvent(this);
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double getAccuracy() {
        return this.accuracy;
    }
}
