/*
 * Enterprise JSF project.
 *
 * Copyright 2023-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.webauthn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.FinishAssertionOptions;
import com.yubico.webauthn.FinishRegistrationOptions;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.attestation.AttestationTrustSource;
import com.yubico.webauthn.data.AttestationConveyancePreference;
import com.yubico.webauthn.data.AttestationObject;
import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.AuthenticatorTransport;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.Extensions;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import com.yubico.webauthn.data.UserIdentity;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.StateHelper;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import org.primefaces.component.api.Widget;
import org.primefaces.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(WebAuthnComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "ejsf", name = "webauthn/webauthn-json.browser-global.js"),
    @ResourceDependency(library = "ejsf", name = "webauthn.js")
})
public class WebAuthnComponent extends UIComponentBase implements Widget, ClientBehaviorHolder {

    public static final String COMPONENT_TYPE = "ejsf.webAuthnComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    private static final List<String> EVENT_NAMES = Collections.unmodifiableList(Arrays.asList(WebAuthnRegisteredEvent.NAME,
            WebAuthnAuthenticatedEvent.NAME,
            WebAuthnErrorEvent.NAME));

    private static final Logger LOGGER = LoggerFactory.getLogger(WebAuthnComponent.class);

    public WebAuthnComponent() {
        setRendererType(WebAuthnRenderer.RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public enum PropertyKeys {
        relyingPartyId,
        relyingPartyName,
        publicKeyCredentialCreationOptions,
        credentialsRequest,
        assertionRequest,
        username,
        userId,
        userDisplayName,
        userVerification,
        timeout,
        authenticatorAttachment,
        residentKey,
        attestationTrustSource,
        attestationConveyance,
        allowUntrustedAttestation,
        credentialRepository,
        registrationErrorListener,
        prfListener,
        authenticationErrorListener,
        registrationMessageInterceptor,
        authenticationMessageInterceptor,
        allowOriginPort,
    }

    public String getRelyingPartyId() {
        return (String) getStateHelper().eval(PropertyKeys.relyingPartyId);
    }

    public void setRelyingPartyId(String id) {
        getStateHelper().put(PropertyKeys.relyingPartyId, id);
    }

    public boolean isAllowOriginPort() {
        return (boolean) getStateHelper().eval(PropertyKeys.allowOriginPort, false);
    }

    public void setAllowOriginPort(boolean allowOriginPort) {
        getStateHelper().put(PropertyKeys.allowOriginPort, allowOriginPort);
    }

    public String getRelyingPartyName() {
        return (String) getStateHelper().eval(PropertyKeys.relyingPartyName);
    }

    public void setRelyingPartyName(String name) {
        getStateHelper().put(PropertyKeys.relyingPartyName, name);
    }

    public void setPublicKeyCredentialCreationOptions(String creationOptions) {
        getStateHelper().put(PropertyKeys.publicKeyCredentialCreationOptions, creationOptions);
    }

    public PublicKeyCredentialCreationOptions getPublicKeyCredentialCreationOptions() throws JsonProcessingException {
        String creationOptions = (String) getStateHelper().get(PropertyKeys.publicKeyCredentialCreationOptions);
        LOGGER.debug("creation options: {}", creationOptions);
        PublicKeyCredentialCreationOptions options = PublicKeyCredentialCreationOptions.fromJson(creationOptions);
        return options;
    }

    public void setAssertionRequest(String assertionRequest, String credentialsRequest) {
        StateHelper stateHelper = getStateHelper();
        stateHelper.put(PropertyKeys.assertionRequest, assertionRequest);
        stateHelper.put(PropertyKeys.credentialsRequest, credentialsRequest);
    }

    public String getCredentialsRequest() {
        return (String) getStateHelper().get(PropertyKeys.credentialsRequest);
    }

    public AssertionRequest getAssertionRequest() throws JsonProcessingException {
        AssertionRequest assertionRequest = AssertionRequest.fromJson((String) getStateHelper().get(PropertyKeys.assertionRequest));
        return assertionRequest;
    }

    public void setUsername(String username) {
        getStateHelper().put(PropertyKeys.username, username);
    }

    public String getUsername() {
        return (String) getStateHelper().eval(PropertyKeys.username);
    }

    public void setUserId(byte[] userId) {
        getStateHelper().put(PropertyKeys.userId, userId);
    }

    public byte[] getUserId() {
        return (byte[]) getStateHelper().eval(PropertyKeys.userId);
    }

    public void setUserDisplayName(String userDisplayName) {
        getStateHelper().put(PropertyKeys.userDisplayName, userDisplayName);
    }

    public String getUserDisplayName() {
        return (String) getStateHelper().eval(PropertyKeys.userDisplayName);
    }

    public void setUserVerification(String userVerification) {
        getStateHelper().put(PropertyKeys.userVerification, userVerification);
    }

    public String getUserVerification() {
        return (String) getStateHelper().eval(PropertyKeys.userVerification);
    }

    public void setTimeout(Long timeout) {
        getStateHelper().put(PropertyKeys.timeout, timeout);
    }

    public Long getTimeout() {
        return (Long) getStateHelper().eval(PropertyKeys.timeout);
    }

    public void setAuthenticatorAttachment(String authenticatorAttachment) {
        getStateHelper().put(PropertyKeys.authenticatorAttachment, authenticatorAttachment);
    }

    public String getAuthenticatorAttachment() {
        return (String) getStateHelper().eval(PropertyKeys.authenticatorAttachment);
    }

    public void setResidentKey(String residentKey) {
        getStateHelper().put(PropertyKeys.residentKey, residentKey);
    }

    public String getResidentKey() {
        return (String) getStateHelper().eval(PropertyKeys.residentKey);
    }

    public void setAttestationConveyance(String attestationConveyance) {
        getStateHelper().put(PropertyKeys.attestationConveyance, attestationConveyance);
    }

    public String getAttestationConveyance() {
        return (String) getStateHelper().eval(PropertyKeys.attestationConveyance);
    }

    public void setAllowUntrustedAttestation(boolean allowUntrustedAttestation) {
        getStateHelper().put(PropertyKeys.allowUntrustedAttestation, allowUntrustedAttestation);
    }

    public boolean isAllowUntrustedAttestation() {
        return (boolean) getStateHelper().eval(PropertyKeys.allowUntrustedAttestation, true);
    }

    public void setRegistrationErrorListener(MethodExpression registrationErrorListener) {
        getStateHelper().put(PropertyKeys.registrationErrorListener, registrationErrorListener);
    }

    private void invokeRegistrationErrorListener(WebAuthnRegistrationError error) {
        MethodExpression methodExpression = (MethodExpression) getStateHelper().get(PropertyKeys.registrationErrorListener);
        if (null == methodExpression) {
            return;
        }
        FacesContext facesContext = getFacesContext();
        ELContext elContext = facesContext.getELContext();
        methodExpression.invoke(elContext, new Object[]{error});
    }

    public void setAuthenticationErrorListener(MethodExpression authenticationErrorListener) {
        getStateHelper().put(PropertyKeys.authenticationErrorListener, authenticationErrorListener);
    }

    private void invokeAuthenticationErrorListener(WebAuthnAuthenticationError error) {
        MethodExpression methodExpression = (MethodExpression) getStateHelper().get(PropertyKeys.authenticationErrorListener);
        if (null == methodExpression) {
            return;
        }
        FacesContext facesContext = getFacesContext();
        ELContext elContext = facesContext.getELContext();
        methodExpression.invoke(elContext, new Object[]{error});
    }

    public void setPRFListener(MethodExpression prfListener) {
        getStateHelper().put(PropertyKeys.prfListener, prfListener);
    }

    public boolean hasPRFListener() {
        MethodExpression methodExpression = (MethodExpression) getStateHelper().get(PropertyKeys.prfListener);
        return null != methodExpression;
    }

    public ByteArray getPRFSalt(ByteArray credentialId) {
        MethodExpression methodExpression = (MethodExpression) getStateHelper().get(PropertyKeys.prfListener);
        if (null == methodExpression) {
            return null;
        }
        FacesContext facesContext = getFacesContext();
        ELContext elContext = facesContext.getELContext();
        ByteArray salt = (ByteArray) methodExpression.invoke(elContext, new Object[]{credentialId});
        return salt;
    }

    public CredentialRepository getCredentialRepository() {
        ValueExpression valueExpression = getValueExpression(PropertyKeys.credentialRepository.name());
        FacesContext facesContext = getFacesContext();
        ELContext elContext = facesContext.getELContext();
        return (CredentialRepository) valueExpression.getValue(elContext);
    }

    public AttestationTrustSource getAttestationTrustSource() {
        ValueExpression valueExpression = getValueExpression(PropertyKeys.attestationTrustSource.name());
        if (null == valueExpression) {
            return null;
        }
        FacesContext facesContext = getFacesContext();
        ELContext elContext = facesContext.getELContext();
        return (AttestationTrustSource) valueExpression.getValue(elContext);
    }

    public void setRegistrationMessageInterceptor(MethodExpression registrationMessageInterceptor) {
        getStateHelper().put(PropertyKeys.registrationMessageInterceptor, registrationMessageInterceptor);
    }

    public String invokeRegistrationMessageInterceptor(String registrationRequest, String registrationResponse) {
        MethodExpression methodExpression = (MethodExpression) getStateHelper().get(PropertyKeys.registrationMessageInterceptor);
        if (null == methodExpression) {
            return registrationResponse;
        }
        FacesContext facesContext = getFacesContext();
        ELContext elContext = facesContext.getELContext();
        String newRegistrationResponse = (String) methodExpression.invoke(elContext, new Object[]{registrationRequest, registrationResponse});
        return newRegistrationResponse;
    }

    public void setAuthenticationMessageInterceptor(MethodExpression authenticationMessageInterceptor) {
        getStateHelper().put(PropertyKeys.authenticationMessageInterceptor, authenticationMessageInterceptor);
    }

    public String invokeAuthenticationMessageInterceptor(String authenticationRequest, String autheticationResponse) {
        MethodExpression methodExpression = (MethodExpression) getStateHelper().get(PropertyKeys.authenticationMessageInterceptor);
        if (null == methodExpression) {
            return autheticationResponse;
        }
        FacesContext facesContext = getFacesContext();
        ELContext elContext = facesContext.getELContext();
        String newAuthenticationResponse = (String) methodExpression.invoke(elContext, new Object[]{authenticationRequest, autheticationResponse});
        return newAuthenticationResponse;
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public String getDefaultEventName() {
        return WebAuthnErrorEvent.NAME;
    }

    public RelyingParty getRelyingParty() {
        String relyingPartyId = getRelyingPartyId();
        String relyingPartyName = getRelyingPartyName();
        RelyingPartyIdentity relyingPartyIdentity = RelyingPartyIdentity.builder()
                .id(relyingPartyId)
                .name(relyingPartyName)
                .build();
        CredentialRepository credentialRepository = getCredentialRepository();
        RelyingParty.RelyingPartyBuilder relyingPartyBuilder = RelyingParty.builder()
                .identity(relyingPartyIdentity)
                .credentialRepository(credentialRepository);
        AttestationTrustSource attestationTrustSource = getAttestationTrustSource();
        if (null != attestationTrustSource) {
            LOGGER.debug("setting attestation trust source");
            relyingPartyBuilder.attestationTrustSource(attestationTrustSource);
        }
        boolean allowUntrustedAttestation = isAllowUntrustedAttestation();
        LOGGER.debug("allow untrusted attestation: {}", allowUntrustedAttestation);
        relyingPartyBuilder.allowUntrustedAttestation(allowUntrustedAttestation);
        String attestationConveyance = getAttestationConveyance();
        if (!UIInput.isEmpty(attestationConveyance)) {
            LOGGER.debug("attestation conveyance: {}", attestationConveyance);
            relyingPartyBuilder.attestationConveyancePreference(
                    AttestationConveyancePreference.valueOf(attestationConveyance.toUpperCase()));
        }
        boolean allowOriginPort = isAllowOriginPort();
        relyingPartyBuilder.allowOriginPort(allowOriginPort);
        RelyingParty relyingParty = relyingPartyBuilder.build();
        return relyingParty;
    }

    private boolean isSelfRequest(FacesContext facesContext) {
        String clientId = getClientId(facesContext);
        ExternalContext externalContext = facesContext.getExternalContext();
        Map<String, String> requestParameterMap = externalContext.getRequestParameterMap();
        String partialSourceParam = requestParameterMap.get(Constants.RequestParams.PARTIAL_SOURCE_PARAM);
        return clientId.equals(partialSourceParam);
    }

    @Override
    public void queueEvent(FacesEvent facesEvent) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (isSelfRequest(facesContext) && facesEvent instanceof AjaxBehaviorEvent) {
            ExternalContext externalContext = facesContext.getExternalContext();
            Map<String, String> requestParameterMap = externalContext.getRequestParameterMap();
            String eventName = requestParameterMap.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = getClientId(facesContext);
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) facesEvent;
            if (WebAuthnRegisteredEvent.NAME.equals(eventName)) {
                String createResponse = requestParameterMap.get(clientId + "_registration_response");
                LOGGER.debug("create response: {}", createResponse);
                PublicKeyCredentialCreationOptions publicKeyCredentialCreationOptions;
                try {
                    publicKeyCredentialCreationOptions = getPublicKeyCredentialCreationOptions();
                } catch (JsonProcessingException ex) {
                    LOGGER.error("JSON error: " + ex.getMessage(), ex);
                    return;
                }
                String createRequest;
                try {
                    createRequest = publicKeyCredentialCreationOptions.toJson();
                } catch (JsonProcessingException ex) {
                    LOGGER.error("JSON error: " + ex.getMessage(), ex);
                    return;
                }
                createResponse = invokeRegistrationMessageInterceptor(createRequest, createResponse);
                Boolean prf = WebAuthnUtils.hasPRF(createResponse);
                UserIdentity userIdentity = publicKeyCredentialCreationOptions.getUser();
                String username = userIdentity.getName();
                LOGGER.debug("registration username: {}", username);

                PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> publicKeyCredential;
                try {
                    publicKeyCredential = PublicKeyCredential.parseRegistrationResponseJson(createResponse);
                } catch (IOException ex) {
                    LOGGER.error("I/O error: " + ex.getMessage(), ex);
                    return;
                }
                AuthenticatorAttestationResponse authenticatorAttestationResponse = publicKeyCredential.getResponse();
                AttestationObject attestationObject = authenticatorAttestationResponse.getAttestation();
                String attestationFormat = attestationObject.getFormat();
                LOGGER.debug("attestation format: {}", attestationFormat);
                FinishRegistrationOptions finishRegistrationOptions = FinishRegistrationOptions.builder()
                        .request(publicKeyCredentialCreationOptions)
                        .response(publicKeyCredential)
                        .build();
                RelyingParty relyingParty = getRelyingParty();
                RegistrationResult registrationResult;
                try {
                    registrationResult = relyingParty.finishRegistration(finishRegistrationOptions);
                } catch (Exception ex) {
                    LOGGER.error("registration error: " + ex.getMessage(), ex);
                    WebAuthnRegistrationError error = new WebAuthnRegistrationError(ex.getMessage());
                    invokeRegistrationErrorListener(error);
                    return;
                }
                SortedSet<AuthenticatorTransport> authenticatorTransports
                        = registrationResult.getKeyId().getTransports().orElseGet(TreeSet::new);
                LOGGER.debug("user verified: {}", registrationResult.isUserVerified());
                boolean attestationTrusted = registrationResult.isAttestationTrusted();
                LOGGER.debug("attestation trusted: {}", attestationTrusted);
                LOGGER.debug("attestation type: {}", registrationResult.getAttestationType());
                X509Certificate attestationCertificate = null;
                Optional<List<X509Certificate>> optionalAttestationTrustPath = registrationResult.getAttestationTrustPath();
                if (optionalAttestationTrustPath.isPresent()) {
                    List<X509Certificate> attestationTrustPath = optionalAttestationTrustPath.get();
                    if (!attestationTrustPath.isEmpty()) {
                        attestationCertificate = attestationTrustPath.get(0);
                        LOGGER.debug("attestation certificate subject: {}", attestationCertificate.getSubjectX500Principal());
                    }
                }
                Boolean residentKey = null;
                Optional<ClientRegistrationExtensionOutputs> clientRegExtensionOutputsOptional = registrationResult.getClientExtensionOutputs();
                if (clientRegExtensionOutputsOptional.isPresent()) {
                    ClientRegistrationExtensionOutputs clientRegistrationExtensionOutputs = clientRegExtensionOutputsOptional.get();
                    Optional<Extensions.CredentialProperties.CredentialPropertiesOutput> credPropsOptional = clientRegistrationExtensionOutputs.getCredProps();
                    if (credPropsOptional.isPresent()) {
                        Extensions.CredentialProperties.CredentialPropertiesOutput credProps = credPropsOptional.get();
                        residentKey = credProps.getRk().orElse(null); // :)
                    }
                }
                RegisteredCredential registeredCredential = RegisteredCredential.builder()
                        .credentialId(registrationResult.getKeyId().getId())
                        .userHandle(userIdentity.getId())
                        .publicKeyCose(registrationResult.getPublicKeyCose())
                        .signatureCount(registrationResult.getSignatureCount())
                        .build();
                WebAuthnRegisteredEvent authnRegisteredEvent
                        = new WebAuthnRegisteredEvent(this, behaviorEvent.getBehavior(), username,
                                registeredCredential, authenticatorTransports, userIdentity,
                                authenticatorAttestationResponse, residentKey, attestationCertificate,
                                prf, attestationTrusted);
                authnRegisteredEvent.setPhaseId(facesEvent.getPhaseId());
                super.queueEvent(authnRegisteredEvent);
                return;
            }
            if (WebAuthnAuthenticatedEvent.NAME.equals(eventName)) {
                String getResponse = requestParameterMap.get(clientId + "_authentication_response");
                getResponse = WebAuthnUtils.fixAuthenticationResponse(getResponse);
                // DO NOT LOG PRF RESULTS
                //LOGGER.error("authentication response: {}", getResponse);
                String getRequest = getCredentialsRequest();
                getResponse = invokeAuthenticationMessageInterceptor(getRequest, getResponse);
                ByteArray prf = WebAuthnUtils.getPRFResults(getResponse);
                RelyingParty relyingParty = getRelyingParty();
                PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> pubicKeyCredential;
                try {
                    pubicKeyCredential = PublicKeyCredential.parseAssertionResponseJson(getResponse);
                } catch (IOException ex) {
                    LOGGER.error("I/O error: " + ex.getMessage(), ex);
                    return;
                }
                AssertionRequest assertionRequest;
                try {
                    assertionRequest = getAssertionRequest();
                } catch (JsonProcessingException ex) {
                    LOGGER.error("JSON error: " + ex.getMessage(), ex);
                    return;
                }
                FinishAssertionOptions finishAssertionOptions = FinishAssertionOptions.builder()
                        .request(assertionRequest)
                        .response(pubicKeyCredential).build();
                AssertionResult result;
                try {
                    result = relyingParty.finishAssertion(finishAssertionOptions);
                } catch (Exception ex) { // catch all: also catch signature validation exceptions and such
                    LOGGER.error("assertion failed: " + ex.getMessage(), ex);
                    WebAuthnAuthenticationError error = new WebAuthnAuthenticationError(ex.getMessage());
                    invokeAuthenticationErrorListener(error);
                    return;
                }
                LOGGER.debug("assertion result: {}", result);
                if (result.isSuccess()) {
                    WebAuthnAuthenticatedEvent authnAuthenticatedEvent
                            = new WebAuthnAuthenticatedEvent(this, behaviorEvent.getBehavior(), result, prf);
                    authnAuthenticatedEvent.setPhaseId(facesEvent.getPhaseId());
                    super.queueEvent(authnAuthenticatedEvent);
                } else {
                    LOGGER.warn("assertion failed");
                    WebAuthnAuthenticationError error = new WebAuthnAuthenticationError("Assertion invalid.");
                    invokeAuthenticationErrorListener(error);
                }
                return;
            }
            if (WebAuthnErrorEvent.NAME.equals(eventName)) {
                String errorMessage = requestParameterMap.get(clientId + "_error");
                LOGGER.warn("received client-side error: {}", errorMessage);
                WebAuthnErrorEvent webAuthnErrorEvent = new WebAuthnErrorEvent(this, behaviorEvent.getBehavior(), errorMessage);
                webAuthnErrorEvent.setPhaseId(facesEvent.getPhaseId());
                super.queueEvent(webAuthnErrorEvent);
                return;
            }
        }
        super.queueEvent(facesEvent);
    }

    public static WebAuthnComponent getWebAuthnComponent(FacesContext facesContext) {
        UIViewRoot viewRoot = facesContext.getViewRoot();
        VisitContext visitContext = VisitContext.createVisitContext(facesContext);
        List<WebAuthnComponent> webAuthnComponents = new LinkedList<>();
        viewRoot.visitTree(visitContext, (VisitContext context, UIComponent target) -> {
            if (target instanceof WebAuthnComponent) {
                WebAuthnComponent webAuthnComponent = (WebAuthnComponent) target;
                webAuthnComponents.add(webAuthnComponent);
                return VisitResult.REJECT;
            }
            return VisitResult.ACCEPT;
        });
        if (webAuthnComponents.isEmpty()) {
            throw new FacesException("missing WebAuthn component");
        }
        if (webAuthnComponents.size() > 1) {
            throw new FacesException("multiple WebAuthn components");
        }
        return webAuthnComponents.get(0);
    }
}
