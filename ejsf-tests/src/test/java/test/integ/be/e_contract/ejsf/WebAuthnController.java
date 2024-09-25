/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.integ.be.e_contract.ejsf;

import test.integ.be.e_contract.ejsf.cdi.TestScoped;
import be.e_contract.ejsf.component.webauthn.WebAuthnAuthenticatedEvent;
import be.e_contract.ejsf.component.webauthn.WebAuthnAuthenticationError;
import be.e_contract.ejsf.component.webauthn.WebAuthnErrorEvent;
import be.e_contract.ejsf.component.webauthn.WebAuthnRegisteredEvent;
import be.e_contract.ejsf.component.webauthn.WebAuthnRegistrationError;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.AuthenticatorTransport;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.UserIdentity;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Set;
import org.primefaces.PrimeFaces;

@Named
@TestScoped
public class WebAuthnController implements Serializable {

    @Inject
    private WebAuthnCredentialRepository credentialRepository;

    private String username;

    private boolean registered;

    private boolean authenticated;

    @PostConstruct
    public void postConstruct() {
        this.username = null;
        this.registered = false;
        this.authenticated = false;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isRegistered() {
        return this.registered;
    }

    public boolean isAuthenticated() {
        return this.authenticated;
    }

    public void errorListener(WebAuthnErrorEvent event) {
        String errorMessage = event.getMessage();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String message = "WebAuthn error: " + errorMessage;
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null);
        facesContext.addMessage(null, facesMessage);
    }

    public void registrationErrorListener(WebAuthnRegistrationError error) {
        String message = "WebAuthn registration error: " + error.getErrorMessage();
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, facesMessage);
        PrimeFaces primeFaces = PrimeFaces.current();
        primeFaces.ajax().update(":messages");
    }

    public void registeredListener(WebAuthnRegisteredEvent event) {
        RegisteredCredential registeredCredential = event.getRegisteredCredential();
        String username = event.getUsername();
        Set<AuthenticatorTransport> authenticatorTransports = event.getAuthenticatorTransports();
        UserIdentity userIdentity = event.getUserIdentity();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Registered: " + username, null));
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "User ID: " + userIdentity.getId().getHex(), null));
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Credential ID: " + registeredCredential.getCredentialId().getHex(), null));
        this.credentialRepository.addRegistration(username, registeredCredential, authenticatorTransports, userIdentity);
        this.registered = true;
    }

    public void authenticatedListener(WebAuthnAuthenticatedEvent event) {
        AssertionResult assertionResult = event.getAssertionResult();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Authenticated: " + assertionResult.getUsername(), null));
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "User ID: " + assertionResult.getCredential().getUserHandle().getHex(), null));
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Credential ID: " + assertionResult.getCredential().getCredentialId().getHex(), null));
        ByteArray prf = event.getPrf();
        if (null != prf) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "PRF: " + prf.getHex(), null));
        }
        this.credentialRepository.updateSignatureCount(assertionResult);
        this.authenticated = true;
    }

    public void authenticationErrorListener(WebAuthnAuthenticationError error) {
        String message = "WebAuthn error: " + error.getErrorMessage();
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, facesMessage);
        PrimeFaces primeFaces = PrimeFaces.current();
        primeFaces.ajax().update(":messages");
    }
}
