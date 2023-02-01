/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.geolocation;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import org.primefaces.event.AbstractAjaxBehaviorEvent;

public class GeolocationErrorEvent extends AbstractAjaxBehaviorEvent {

    public static final String NAME = "error";

    private final GeolocationPositionError error;

    private final String message;

    public GeolocationErrorEvent(UIComponent component, Behavior behavior, int code, String message) {
        super(component, behavior);
        this.error = GeolocationPositionError.fromCode(code);
        this.message = message;
    }

    public GeolocationPositionError getError() {
        return this.error;
    }

    public String getMessage() {
        return this.message;
    }
}
