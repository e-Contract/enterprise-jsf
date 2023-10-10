/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import be.e_contract.ejsf.component.webauthn.WebAuthnAuthenticatedEvent;
import be.e_contract.ejsf.component.webauthn.WebAuthnErrorEvent;
import be.e_contract.ejsf.component.webauthn.WebAuthnRegisteredEvent;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.AuthenticatorTransport;
import com.yubico.webauthn.data.UserIdentity;
import java.io.Serializable;
import java.security.SecureRandom;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Set;
import javax.faces.context.ExternalContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("webAuthnController")
@ViewScoped
public class WebAuthnController implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebAuthnController.class);

    @Inject
    private WebAuthnCredentialRepository credentialRepository;

    private String username;

    private String userVerification;

    private String authenticatorAttachment;

    private String residentKey;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserVerification() {
        return this.userVerification;
    }

    public void setUserVerification(String userVerification) {
        this.userVerification = userVerification;
    }

    public String getAuthenticatorAttachment() {
        return this.authenticatorAttachment;
    }

    public void setAuthenticatorAttachment(String authenticatorAttachment) {
        this.authenticatorAttachment = authenticatorAttachment;
    }

    public String getResidentKey() {
        return this.residentKey;
    }

    public void setResidentKey(String residentKey) {
        this.residentKey = residentKey;
    }

    public void registeredListener(WebAuthnRegisteredEvent event) {
        RegisteredCredential registeredCredential = event.getRegisteredCredential();
        String username = event.getUsername();
        Set<AuthenticatorTransport> authenticatorTransports = event.getAuthenticatorTransports();
        UserIdentity userIdentity = event.getUserIdentity();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Registered " + username, null));
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "User ID " + userIdentity.getId().getHex(), null));
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Credential ID " + registeredCredential.getCredentialId().getHex(), null));
        this.credentialRepository.addRegistration(username, registeredCredential, authenticatorTransports, userIdentity);
    }

    public void authenticatedListener(WebAuthnAuthenticatedEvent event) {
        AssertionResult assertionResult = event.getAssertionResult();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Authenticated " + assertionResult.getUsername(), null));
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "User ID " + assertionResult.getCredential().getUserHandle().getHex(), null));
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Credential ID " + assertionResult.getCredential().getCredentialId().getHex(), null));
    }

    public void errorListener(WebAuthnErrorEvent event) {
        String errorMessage = event.getMessage();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String message = "WebAuthn error: " + errorMessage;
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null);
        facesContext.addMessage(null, facesMessage);
    }

    public String getRelyingPartyId() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        String requestServerName = externalContext.getRequestServerName();
        // production configuration should always return the same value
        if ("localhost".equals(requestServerName)) {
            return "localhost";
        }
        return "demo.e-contract.be";
    }

    public byte[] getUserId() {
        LOGGER.debug("getUserId");
        byte[] userId = new byte[32];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(userId);
        return userId;
    }
}
