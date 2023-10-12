/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.webauthn;

import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.UserIdentity;
import com.yubico.webauthn.StartAssertionOptions;
import com.yubico.webauthn.data.AuthenticatorAttachment;
import com.yubico.webauthn.data.AuthenticatorSelectionCriteria;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.ResidentKeyRequirement;
import com.yubico.webauthn.data.UserVerificationRequirement;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.render.FacesRenderer;
import org.primefaces.PrimeFaces;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesRenderer(componentFamily = WebAuthnComponent.COMPONENT_FAMILY, rendererType = WebAuthnRenderer.RENDERER_TYPE)
public class WebAuthnRenderer extends CoreRenderer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebAuthnRenderer.class);

    public static final String RENDERER_TYPE = "ejsf.webAuthnRenderer";

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        WebAuthnComponent webAuthnComponent = (WebAuthnComponent) component;
        encodeScript(context, webAuthnComponent);
    }

    private void encodeScript(FacesContext context, WebAuthnComponent webAuthnComponent) throws IOException {
        WidgetBuilder widgetBuilder = getWidgetBuilder(context);
        widgetBuilder.init("EJSFWebAuthn", webAuthnComponent);
        encodeClientBehaviors(context, webAuthnComponent);
        widgetBuilder.finish();
    }

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
        ExternalContext externalContext = context.getExternalContext();
        Map<String, String> requestParameterMap = externalContext.getRequestParameterMap();
        String clientId = component.getClientId(context);
        if (requestParameterMap.containsKey(clientId + "_registration_request")) {
            LOGGER.debug("registration request");
            WebAuthnComponent webAuthnComponent = (WebAuthnComponent) component;
            RelyingParty relyingParty = webAuthnComponent.getRelyingParty();

            byte[] userIdData = webAuthnComponent.getUserId();
            if (null == userIdData) {
                LOGGER.debug("we generate userId");
                userIdData = new byte[32];
                SecureRandom secureRandom = new SecureRandom();
                secureRandom.nextBytes(userIdData);
            }
            ByteArray userId = new ByteArray(userIdData);
            LOGGER.debug("userId: {}", userId);
            String username = webAuthnComponent.getUsername();
            LOGGER.debug("username: {}", username);
            String userDisplayName = webAuthnComponent.getUserDisplayName();
            if (null == userDisplayName) {
                userDisplayName = username;
            }
            UserIdentity userIdentity = UserIdentity.builder()
                    .name(username)
                    .displayName(userDisplayName)
                    .id(userId)
                    .build();
            StartRegistrationOptions.StartRegistrationOptionsBuilder startRegistrationOptionsBuilder
                    = StartRegistrationOptions.builder().user(userIdentity);
            String userVerification = webAuthnComponent.getUserVerification();
            String authenticatorAttachment = webAuthnComponent.getAuthenticatorAttachment();
            String residentKey = webAuthnComponent.getResidentKey();
            if (!UIInput.isEmpty(userVerification) || !UIInput.isEmpty(authenticatorAttachment)
                    || !UIInput.isEmpty(residentKey)) {
                AuthenticatorSelectionCriteria.AuthenticatorSelectionCriteriaBuilder authenticatorSelectionCriteriaBuilder
                        = AuthenticatorSelectionCriteria.builder();
                if (!UIInput.isEmpty(userVerification)) {
                    authenticatorSelectionCriteriaBuilder
                            .userVerification(UserVerificationRequirement.valueOf(userVerification.toUpperCase()));
                }
                if (!UIInput.isEmpty(authenticatorAttachment)) {
                    if ("platform".equals(authenticatorAttachment)) {
                        authenticatorSelectionCriteriaBuilder.authenticatorAttachment(AuthenticatorAttachment.PLATFORM);
                    } else if ("cross-platform".equals(authenticatorAttachment)) {
                        authenticatorSelectionCriteriaBuilder.authenticatorAttachment(AuthenticatorAttachment.CROSS_PLATFORM);
                    }
                }
                if (!UIInput.isEmpty(residentKey)) {
                    authenticatorSelectionCriteriaBuilder
                            .residentKey(ResidentKeyRequirement.valueOf(residentKey.toUpperCase()));
                }
                AuthenticatorSelectionCriteria authenticatorSelectionCriteria = authenticatorSelectionCriteriaBuilder.build();
                startRegistrationOptionsBuilder.authenticatorSelection(authenticatorSelectionCriteria);
            }
            Long timeout = webAuthnComponent.getTimeout();
            if (null != timeout) {
                startRegistrationOptionsBuilder.timeout(timeout);
            }
            StartRegistrationOptions startRegistrationOptions = startRegistrationOptionsBuilder.build();
            PublicKeyCredentialCreationOptions publicKeyCredentialCreationOptions = relyingParty.startRegistration(startRegistrationOptions);
            String request = WebAuthnUtils.toJson(publicKeyCredentialCreationOptions);
            if (null == request) {
                return;
            }
            webAuthnComponent.setPublicKeyCredentialCreationOptions(request);
            PrimeFaces primeFaces = PrimeFaces.current();
            PrimeFaces.Ajax ajax = primeFaces.ajax();
            ajax.addCallbackParam("publicKeyCredentialCreationOptions", request);
            context.renderResponse();
        } else if (requestParameterMap.containsKey(clientId + "_authentication_request")) {
            LOGGER.debug("authentication request");
            WebAuthnComponent webAuthnComponent = (WebAuthnComponent) component;
            RelyingParty relyingParty = webAuthnComponent.getRelyingParty();
            String username = webAuthnComponent.getUsername();
            LOGGER.debug("username: {}", username);
            StartAssertionOptions.StartAssertionOptionsBuilder startAssertionOptionsBuilder = StartAssertionOptions.builder()
                    .username(username);
            String userVerification = webAuthnComponent.getUserVerification();
            if (!UIInput.isEmpty(userVerification)) {
                LOGGER.debug("user verification: {}", userVerification);
                startAssertionOptionsBuilder.userVerification(UserVerificationRequirement.valueOf(userVerification.toUpperCase()));
            }
            Long timeout = webAuthnComponent.getTimeout();
            if (null != timeout) {
                startAssertionOptionsBuilder.timeout(timeout);
            }
            StartAssertionOptions startAssertionOptions = startAssertionOptionsBuilder
                    .build();
            AssertionRequest assertionRequest = relyingParty.startAssertion(startAssertionOptions);
            String credentialsRequest = WebAuthnUtils.toCredentialsJson(assertionRequest);
            if (null == credentialsRequest) {
                return;
            }
            String assertionRequestJson = WebAuthnUtils.toJson(assertionRequest);
            if (null == assertionRequestJson) {
                return;
            }
            webAuthnComponent.setAssertionRequest(assertionRequestJson);
            PrimeFaces primeFaces = PrimeFaces.current();
            PrimeFaces.Ajax ajax = primeFaces.ajax();
            ajax.addCallbackParam("publicKeyCredentialRequestOptions", credentialsRequest);
            context.renderResponse();
        }
    }
}
