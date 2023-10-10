/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.webauthn;

import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.AuthenticatorTransport;
import com.yubico.webauthn.data.UserIdentity;
import java.util.Set;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import org.primefaces.event.AbstractAjaxBehaviorEvent;

public class WebAuthnRegisteredEvent extends AbstractAjaxBehaviorEvent {

    public static final String NAME = "registered";

    private final String username;

    private final RegisteredCredential registeredCredential;

    private final Set<AuthenticatorTransport> authenticatorTransports;

    private final UserIdentity userIdentity;

    public WebAuthnRegisteredEvent(UIComponent component, Behavior behavior, String username,
            RegisteredCredential registeredCredential,
            Set<AuthenticatorTransport> authenticatorTransports,
            UserIdentity userIdentity) {
        super(component, behavior);
        this.username = username;
        this.registeredCredential = registeredCredential;
        this.authenticatorTransports = authenticatorTransports;
        this.userIdentity = userIdentity;
    }

    public String getUsername() {
        return this.username;
    }

    public RegisteredCredential getRegisteredCredential() {
        return this.registeredCredential;
    }

    public Set<AuthenticatorTransport> getAuthenticatorTransports() {
        return this.authenticatorTransports;
    }

    public UserIdentity getUserIdentity() {
        return this.userIdentity;
    }
}
