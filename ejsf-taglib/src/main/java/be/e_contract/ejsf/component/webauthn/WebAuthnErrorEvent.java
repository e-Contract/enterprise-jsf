/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.webauthn;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import org.primefaces.event.AbstractAjaxBehaviorEvent;

public class WebAuthnErrorEvent extends AbstractAjaxBehaviorEvent {

    public static final String NAME = "error";

    private final String message;

    public WebAuthnErrorEvent(UIComponent component, Behavior behavior, String message) {
        super(component, behavior);
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
