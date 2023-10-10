/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
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
import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.AuthenticatorTransport;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import com.yubico.webauthn.data.UserIdentity;
import com.yubico.webauthn.exception.AssertionFailedException;
import com.yubico.webauthn.exception.RegistrationFailedException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
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
    @ResourceDependency(library = "webauthn", name = "webauthn-json.browser-global.js"),
    @ResourceDependency(library = "ejsf", name = "webauthn.js")
})
public class WebAuthnComponent extends UIComponentBase implements Widget, ClientBehaviorHolder {

    public static final String COMPONENT_TYPE = "ejsf.webAuthnComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

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
        PublicKeyCredentialCreationOptions,
        AssertionRequest,
        username,
        userId,
        userDisplayName,
        userVerification,
        timeout,
        authenticatorAttachment,
        residentKey,
    }

    public String getRelyingPartyId() {
        return (String) getStateHelper().eval(PropertyKeys.relyingPartyId);
    }

    public void setRelyingPartyId(String id) {
        getStateHelper().put(PropertyKeys.relyingPartyId, id);
    }

    public String getRelyingPartyName() {
        return (String) getStateHelper().eval(PropertyKeys.relyingPartyName);
    }

    public void setRelyingPartyName(String name) {
        getStateHelper().put(PropertyKeys.relyingPartyName, name);
    }

    public void setPublicKeyCredentialCreationOptions(String creationOptions) {
        getStateHelper().put(PropertyKeys.PublicKeyCredentialCreationOptions, creationOptions);
    }

    public PublicKeyCredentialCreationOptions getPublicKeyCredentialCreationOptions() throws JsonProcessingException {
        PublicKeyCredentialCreationOptions options = PublicKeyCredentialCreationOptions.fromJson((String) getStateHelper().get(PropertyKeys.PublicKeyCredentialCreationOptions));
        // consume only once
        getStateHelper().put(PropertyKeys.PublicKeyCredentialCreationOptions, null);
        return options;
    }

    public void setAssertionRequest(String assertionRequest) {
        getStateHelper().put(PropertyKeys.AssertionRequest, assertionRequest);
    }

    public AssertionRequest getAssertionRequest() throws JsonProcessingException {
        AssertionRequest assertionRequest = AssertionRequest.fromJson((String) getStateHelper().get(PropertyKeys.AssertionRequest));
        getStateHelper().put(PropertyKeys.AssertionRequest, null);
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

    @Override
    public void setValueExpression(String name, ValueExpression binding) {
        LOGGER.debug("setValueExpression: {}", name);
        super.setValueExpression(name, binding);
    }

    public CredentialRepository getCredentialRepository() {
        ValueExpression valueExpression = getValueExpression("credentialRepository");
        FacesContext facesContext = getFacesContext();
        ELContext elContext = facesContext.getELContext();
        return (CredentialRepository) valueExpression.getValue(elContext);
    }

    @Override
    public Collection<String> getEventNames() {
        return Arrays.asList(WebAuthnRegisteredEvent.NAME, WebAuthnAuthenticatedEvent.NAME, WebAuthnErrorEvent.NAME);
    }

    @Override
    public String getDefaultEventName() {
        return WebAuthnRegisteredEvent.NAME;
    }

    public RelyingParty getRelyingParty() {
        String relyingPartyId = getRelyingPartyId();
        String relyingPartyName = getRelyingPartyName();
        RelyingPartyIdentity relyingPartyIdentity = RelyingPartyIdentity.builder()
                .id(relyingPartyId)
                .name(relyingPartyName)
                .build();
        CredentialRepository credentialRepository = getCredentialRepository();
        RelyingParty relyingParty = RelyingParty.builder()
                .identity(relyingPartyIdentity)
                .credentialRepository(credentialRepository)
                .build();
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
                UserIdentity userIdentity = publicKeyCredentialCreationOptions.getUser();
                String username = userIdentity.getName();
                LOGGER.debug("username: {}", username);

                PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> publicKeyCredential;
                try {
                    publicKeyCredential = PublicKeyCredential.parseRegistrationResponseJson(createResponse);
                } catch (IOException ex) {
                    LOGGER.error("I/O error: " + ex.getMessage(), ex);
                    return;
                }
                FinishRegistrationOptions finishRegistrationOptions = FinishRegistrationOptions.builder()
                        .request(publicKeyCredentialCreationOptions)
                        .response(publicKeyCredential)
                        .build();
                RelyingParty relyingParty = getRelyingParty();
                RegistrationResult registrationResult;
                try {
                    registrationResult = relyingParty.finishRegistration(finishRegistrationOptions);
                } catch (RegistrationFailedException ex) {
                    LOGGER.error("registration error: " + ex.getMessage(), ex);
                    return;
                }
                SortedSet<AuthenticatorTransport> authenticatorTransports
                        = registrationResult.getKeyId().getTransports().orElseGet(TreeSet::new);
                LOGGER.debug("user verified: {}", registrationResult.isUserVerified());
                RegisteredCredential registeredCredential = RegisteredCredential.builder()
                        .credentialId(registrationResult.getKeyId().getId())
                        .userHandle(userIdentity.getId())
                        .publicKeyCose(registrationResult.getPublicKeyCose())
                        .signatureCount(registrationResult.getSignatureCount())
                        .build();
                WebAuthnRegisteredEvent authnRegisteredEvent
                        = new WebAuthnRegisteredEvent(this, behaviorEvent.getBehavior(), username,
                                registeredCredential, authenticatorTransports, userIdentity);
                authnRegisteredEvent.setPhaseId(facesEvent.getPhaseId());
                super.queueEvent(authnRegisteredEvent);
                return;
            }
            if (WebAuthnAuthenticatedEvent.NAME.equals(eventName)) {
                String getResponse = requestParameterMap.get(clientId + "_authentication_response");
                LOGGER.debug("authentication response: {}", getResponse);
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
                } catch (AssertionFailedException ex) {
                    LOGGER.error("assertion failed: " + ex.getMessage(), ex);
                    return;
                }
                LOGGER.debug("assertion result: {}", result);
                if (result.isSuccess()) {
                    WebAuthnAuthenticatedEvent authnAuthenticatedEvent
                            = new WebAuthnAuthenticatedEvent(this, behaviorEvent.getBehavior(), result);
                    authnAuthenticatedEvent.setPhaseId(facesEvent.getPhaseId());
                    super.queueEvent(authnAuthenticatedEvent);
                } else {
                    LOGGER.warn("assertion failed");
                }
                return;
            }
            if (WebAuthnErrorEvent.NAME.equals(eventName)) {
                String errorMessage = requestParameterMap.get(clientId + "_error");
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
