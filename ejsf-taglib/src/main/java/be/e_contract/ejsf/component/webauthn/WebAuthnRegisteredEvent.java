/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.webauthn;

import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.AuthenticatorTransport;
import com.yubico.webauthn.data.UserIdentity;
import java.security.cert.X509Certificate;
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

    private final AuthenticatorAttestationResponse authenticatorAttestationResponse;

    private final Boolean residentKey;

    private final X509Certificate attestationCertificate;

    private final Boolean prf;

    private final boolean attestationTrusted;

    public WebAuthnRegisteredEvent(UIComponent component, Behavior behavior, String username,
            RegisteredCredential registeredCredential,
            Set<AuthenticatorTransport> authenticatorTransports,
            UserIdentity userIdentity,
            AuthenticatorAttestationResponse authenticatorAttestationResponse,
            Boolean residentKey, X509Certificate attestationCertificate,
            Boolean prf, boolean attestationTrusted) {
        super(component, behavior);
        this.username = username;
        this.registeredCredential = registeredCredential;
        this.authenticatorTransports = authenticatorTransports;
        this.userIdentity = userIdentity;
        this.authenticatorAttestationResponse = authenticatorAttestationResponse;
        this.residentKey = residentKey;
        this.attestationCertificate = attestationCertificate;
        this.prf = prf;
        this.attestationTrusted = attestationTrusted;
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

    public AuthenticatorAttestationResponse getAuthenticatorAttestationResponse() {
        return this.authenticatorAttestationResponse;
    }

    public Boolean getResidentKey() {
        return this.residentKey;
    }

    public X509Certificate getAttestationCertificate() {
        return this.attestationCertificate;
    }

    public Boolean getPrf() {
        return this.prf;
    }

    public boolean isAttestationTrusted() {
        return this.attestationTrusted;
    }
}
