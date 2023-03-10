/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.geolocation;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import org.primefaces.event.AbstractAjaxBehaviorEvent;

public class GeolocationPositionEvent extends AbstractAjaxBehaviorEvent {

    public static final String NAME = "geolocation";

    private final double latitude;

    private final double longitude;

    private final double accuracy;

    public GeolocationPositionEvent(UIComponent component, Behavior behavior, double latitude, double longitude, double accuracy) {
        super(component, behavior);
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
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
