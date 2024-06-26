/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import be.e_contract.ejsf.component.webauthn.WebAuthnAuthenticatedEvent;
import be.e_contract.ejsf.component.webauthn.WebAuthnAuthenticationError;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Optional;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.Set;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.faces.context.ExternalContext;
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
        this.userVerification = "preferred";
        this.authenticatorAttachment = "cross-platform";
        this.residentKey = "preferred";
        this.attestationConveyance = "direct";
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
        if (null != event.getResidentKey()) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Resident key: " + event.getResidentKey(), null));
        }
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Attestation trusted: " + event.isAttestationTrusted(), null));
        if (null != event.getAttestationCertificate()) {
            X509Certificate attestationCertificate = event.getAttestationCertificate();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Attestation certificate: " + attestationCertificate.getSubjectX500Principal(), null));
        }
        if (null != event.getPrf()) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "PRF enabled: " + event.getPrf(), null));
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "PRF not supported", null));
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
        ByteArray prf = event.getPrf();
        if (null != prf) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "PRF: " + prf.getHex(), null));
        }
        this.credentialRepository.updateSignatureCount(assertionResult);
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

    public void authenticationErrorListener(WebAuthnAuthenticationError error) {
        String message = "WebAuthn error: " + error.getErrorMessage();
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, facesMessage);
        PrimeFaces primeFaces = PrimeFaces.current();
        primeFaces.ajax().update(":messages");
    }

    public ByteArray prfListener(ByteArray credentialId) {
        LOGGER.debug("PRF listener: {}", credentialId.getHex());
        // of course you don't do this in production
        // just want to verify a deterministic PRF result here
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            LOGGER.error("algo error: " + ex.getMessage(), ex);
            return credentialId;
        }
        // make sure we return at least 32 bytes
        byte[] salt = messageDigest.digest(credentialId.getBytes());
        return new ByteArray(salt);
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

    public String registrationMessageInterceptor(String request, String response) {
        LOGGER.debug("registration request: {}", request);
        LOGGER.debug("registration response: {}", response);
        return response;
    }

    public String authenticationMessageInterceptor(String request, String response) {
        LOGGER.debug("authentication request: {}", request);
        LOGGER.debug("authentication response: {}", response);
        return response;
    }
}
