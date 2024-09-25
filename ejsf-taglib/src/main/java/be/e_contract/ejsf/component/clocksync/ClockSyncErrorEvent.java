/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.clocksync;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import org.primefaces.event.AbstractAjaxBehaviorEvent;

public class ClockSyncErrorEvent extends AbstractAjaxBehaviorEvent {

    public static final String NAME = "error";

    private final String errorMessage;

    public ClockSyncErrorEvent(UIComponent component, Behavior behavior, String errorMessage) {
        super(component, behavior);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}
