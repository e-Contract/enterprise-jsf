/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.integ.be.e_contract.ejsf;

import be.e_contract.ejsf.component.webauthn.WebAuthnErrorEvent;
import be.e_contract.ejsf.component.webauthn.WebAuthnRegisteredEvent;
import be.e_contract.ejsf.component.webauthn.WebAuthnRegistrationError;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.AuthenticatorTransport;
import com.yubico.webauthn.data.UserIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Set;
import org.primefaces.PrimeFaces;

@Named
@ApplicationScoped
public class WebAuthnController implements Serializable {

    @Inject
    private WebAuthnCredentialRepository credentialRepository;

    private String username;

    private boolean registered;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isRegistered() {
        return this.registered;
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
}
