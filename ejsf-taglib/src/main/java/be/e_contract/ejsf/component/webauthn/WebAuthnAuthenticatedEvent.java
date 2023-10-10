/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.webauthn;

import com.yubico.webauthn.AssertionResult;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import org.primefaces.event.AbstractAjaxBehaviorEvent;

public class WebAuthnAuthenticatedEvent extends AbstractAjaxBehaviorEvent {

    public static final String NAME = "authenticated";

    private final AssertionResult assertionResult;

    public WebAuthnAuthenticatedEvent(UIComponent component, Behavior behavior, AssertionResult assertionResult) {
        super(component, behavior);
        this.assertionResult = assertionResult;
    }

    public AssertionResult getAssertionResult() {
        return this.assertionResult;
    }
}
