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
import be.e_contract.ejsf.component.webauthn.WebAuthnRegistrationError;
import com.yubico.fido.metadata.AAGUID;
import com.yubico.fido.metadata.FidoMetadataDownloader;
import com.yubico.fido.metadata.FidoMetadataService;
import com.yubico.fido.metadata.MetadataBLOB;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.attestation.AttestationTrustSource;
import com.yubico.webauthn.data.AttestationObject;
import com.yubico.webauthn.data.AttestedCredentialData;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.AuthenticatorData;
import com.yubico.webauthn.data.AuthenticatorTransport;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.UserIdentity;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Optional;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.ExternalContext;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("webAuthnController")
@ViewScoped
public class WebAuthnController implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebAuthnController.class);

    @Inject
    private WebAuthnCredentialRepository credentialRepository;

    @Inject
    private AAGUIDRepository aaguidRepository;

    private String username;

    private String userVerification;

    private String authenticatorAttachment;

    private String residentKey;

    private String attestationConveyance;

    private boolean allowUntrustedAttestation;

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

    public String getAttestationConveyance() {
        return this.attestationConveyance;
    }

    public void setAttestationConveyance(String attestationConveyance) {
        this.attestationConveyance = attestationConveyance;
    }

    public boolean isAllowUntrustedAttestation() {
        return this.allowUntrustedAttestation;
    }

    public void setAllowUntrustedAttestation(boolean allowUntrustedAttestation) {
        this.allowUntrustedAttestation = allowUntrustedAttestation;
    }

    @PostConstruct
    public void postConstruct() {
        this.allowUntrustedAttestation = true;
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
        AuthenticatorAttestationResponse authenticatorAttestationResponse = event.getAuthenticatorAttestationResponse();
        AttestationObject attestationObject = authenticatorAttestationResponse.getAttestation();
        AuthenticatorData authenticatorData = attestationObject.getAuthenticatorData();
        Optional<AttestedCredentialData> attestedCredentialDataOptional = authenticatorData.getAttestedCredentialData();
        if (attestedCredentialDataOptional.isPresent()) {
            AttestedCredentialData attestedCredentialData = attestedCredentialDataOptional.get();
            ByteArray aaguidByteArray = attestedCredentialData.getAaguid();
            AAGUID aaguid = new AAGUID(aaguidByteArray);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "AAGUID: " + aaguid.asGuidString(), null));
            String authenticatorName = this.aaguidRepository.findName(aaguid.asGuidString());
            if (null != authenticatorName) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Authenticator: " + authenticatorName, null));
            }
        }
        this.credentialRepository.addRegistration(username, registeredCredential, authenticatorTransports, userIdentity);
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
    }

    public void errorListener(WebAuthnErrorEvent event) {
        String errorMessage = event.getMessage();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String message = "WebAuthn error: " + errorMessage;
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null);
        facesContext.addMessage(null, facesMessage);
    }

    public void registrationErrorListener(WebAuthnRegistrationError error) {
        String message = "WebAuthn error: " + error.getErrorMessage();
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, facesMessage);
        PrimeFaces primeFaces = PrimeFaces.current();
        primeFaces.ajax().update(":messages");
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

    @Produces
    @ApplicationScoped
    @Named("webAuthnAttestationTrustSource")
    public AttestationTrustSource createAttestationTrustSource() {
        LOGGER.debug("creating AttestationTrustSource");
        System.setProperty("com.sun.security.enableCRLDP", "true");
        File trustRootCacheFile;
        File blobCacheFile;
        try {
            trustRootCacheFile = File.createTempFile("fido-mds-trust-root-", ".bin");
            blobCacheFile = File.createTempFile("fido-mds-blob-", ".bin");
        } catch (IOException ex) {
            LOGGER.error("I/O error: " + ex.getMessage(), ex);
            return null;
        }
        FidoMetadataDownloader downloader = FidoMetadataDownloader.builder()
                .expectLegalHeader("Retrieval and use of this BLOB indicates acceptance of the appropriate agreement located at https://fidoalliance.org/metadata/metadata-legal-terms/")
                .useDefaultTrustRoot()
                .useTrustRootCacheFile(trustRootCacheFile)
                .useDefaultBlob()
                .useBlobCacheFile(blobCacheFile)
                .verifyDownloadsOnly(true)
                .build();

        FidoMetadataService mds;
        try {
            MetadataBLOB metadataBlob = downloader.loadCachedBlob();
            mds = FidoMetadataService.builder()
                    .useBlob(metadataBlob)
                    .build();
        } catch (Exception ex) {
            LOGGER.error("error: " + ex.getMessage(), ex);
            return null;
        }
        LOGGER.debug("trust root cache file: {}", trustRootCacheFile.getAbsolutePath());
        LOGGER.debug("blob cache file: {}", blobCacheFile.getAbsolutePath());
        return mds;
    }
}
