/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.webauthn;

import com.yubico.webauthn.attestation.AttestationTrustSource;
import java.io.IOException;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagException;
import javax.faces.view.facelets.TagHandler;
import org.primefaces.behavior.ajax.AjaxBehavior;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebAuthnRegistrationTagHandler extends TagHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebAuthnRegistrationTagHandler.class);

    public WebAuthnRegistrationTagHandler(TagConfig tagConfig) {
        super(tagConfig);
    }

    @Override
    public void apply(FaceletContext context, UIComponent parent) throws IOException {
        if (!ComponentHandler.isNew(parent)) {
            return;
        }
        if (!(parent instanceof WebAuthnComponent)) {
            throw new TagException(this.tag, "parent must be WebAuthnComponent");
        }
        WebAuthnComponent webAuthnComponent = (WebAuthnComponent) parent;
        FacesContext facesContext = context.getFacesContext();
        Application application = facesContext.getApplication();
        AjaxBehavior ajaxBehavior = (AjaxBehavior) application.createBehavior(AjaxBehavior.BEHAVIOR_ID);

        TagAttribute updateTagAttribute = getAttribute("update");
        if (null != updateTagAttribute) {
            String update = updateTagAttribute.getValue();
            ajaxBehavior.setUpdate(update);
        }

        TagAttribute userIdTagAttribute = getAttribute(WebAuthnComponent.PropertyKeys.userId.name());
        if (null != userIdTagAttribute) {
            ValueExpression userIdValueExpression = userIdTagAttribute.getValueExpression(context, byte[].class);
            webAuthnComponent.setValueExpression(WebAuthnComponent.PropertyKeys.userId.name(), userIdValueExpression);
        }

        TagAttribute userDisplayNameTagAttribute = getAttribute(WebAuthnComponent.PropertyKeys.userDisplayName.name());
        if (null != userDisplayNameTagAttribute) {
            ValueExpression userDisplayNameValueExpression = userDisplayNameTagAttribute.getValueExpression(context, String.class);
            webAuthnComponent.setValueExpression(WebAuthnComponent.PropertyKeys.userDisplayName.name(), userDisplayNameValueExpression);
        }

        TagAttribute userVerificationTagAttribute = getAttribute(WebAuthnComponent.PropertyKeys.userVerification.name());
        if (null != userVerificationTagAttribute) {
            ValueExpression userVerificationValueExpression = userVerificationTagAttribute.getValueExpression(context, String.class);
            webAuthnComponent.setValueExpression(WebAuthnComponent.PropertyKeys.userVerification.name(), userVerificationValueExpression);
        } else {
            webAuthnComponent.setValueExpression(WebAuthnComponent.PropertyKeys.userVerification.name(), null);
        }

        TagAttribute timeoutTagAttribute = getAttribute(WebAuthnComponent.PropertyKeys.timeout.name());
        if (null != timeoutTagAttribute) {
            ValueExpression timeoutValueExpression = timeoutTagAttribute.getValueExpression(context, Long.class);
            webAuthnComponent.setValueExpression(WebAuthnComponent.PropertyKeys.timeout.name(), timeoutValueExpression);
        } else {
            webAuthnComponent.setValueExpression(WebAuthnComponent.PropertyKeys.timeout.name(), null);
        }

        TagAttribute authenticatorAttachmentTagAttribute = getAttribute(WebAuthnComponent.PropertyKeys.authenticatorAttachment.name());
        if (null != authenticatorAttachmentTagAttribute) {
            ValueExpression authenticatorAttachmentValueExpression = authenticatorAttachmentTagAttribute.getValueExpression(context, String.class);
            webAuthnComponent.setValueExpression(WebAuthnComponent.PropertyKeys.authenticatorAttachment.name(), authenticatorAttachmentValueExpression);
        }

        TagAttribute residentKeyTagAttribute = getAttribute(WebAuthnComponent.PropertyKeys.residentKey.name());
        if (null != residentKeyTagAttribute) {
            ValueExpression residentKeyValueExpression = residentKeyTagAttribute.getValueExpression(context, String.class);
            webAuthnComponent.setValueExpression(WebAuthnComponent.PropertyKeys.residentKey.name(), residentKeyValueExpression);
        }

        TagAttribute attestationTrustSourceTagAttribute = getAttribute(WebAuthnComponent.PropertyKeys.attestationTrustSource.name());
        if (null != attestationTrustSourceTagAttribute) {
            ValueExpression attestationTrustSourceValueExpression = attestationTrustSourceTagAttribute.getValueExpression(context, AttestationTrustSource.class);
            webAuthnComponent.setValueExpression(WebAuthnComponent.PropertyKeys.attestationTrustSource.name(), attestationTrustSourceValueExpression);
        }

        TagAttribute attestationConveyanceTagAttribute = getAttribute(WebAuthnComponent.PropertyKeys.attestationConveyance.name());
        if (null != attestationConveyanceTagAttribute) {
            ValueExpression attestationConveyanceValueExpression = attestationConveyanceTagAttribute.getValueExpression(context, String.class);
            webAuthnComponent.setValueExpression(WebAuthnComponent.PropertyKeys.attestationConveyance.name(), attestationConveyanceValueExpression);
        }

        TagAttribute allowUntrustedAttestationTagAttribute = getAttribute(WebAuthnComponent.PropertyKeys.allowUntrustedAttestation.name());
        if (null != allowUntrustedAttestationTagAttribute) {
            ValueExpression allowUntrustedAttestationValueExpression = allowUntrustedAttestationTagAttribute.getValueExpression(context, Boolean.class);
            webAuthnComponent.setValueExpression(WebAuthnComponent.PropertyKeys.allowUntrustedAttestation.name(), allowUntrustedAttestationValueExpression);
        }

        TagAttribute errorListenerTagAttribute = getAttribute("errorListener");
        if (null != errorListenerTagAttribute) {
            MethodExpression errorListenerMethodExpression = errorListenerTagAttribute.getMethodExpression(context, void.class, new Class[]{
                WebAuthnRegistrationError.class
            });
            webAuthnComponent.setRegistrationErrorListener(errorListenerMethodExpression);
        }

        TagAttribute messageInterceptorTagAttribute = getAttribute("messageInterceptor");
        if (null != messageInterceptorTagAttribute) {
            MethodExpression messageInterceptorMethodExpression = messageInterceptorTagAttribute.getMethodExpression(context, String.class, new Class[]{
                String.class, String.class
            });
            webAuthnComponent.setRegistrationMessageInterceptor(messageInterceptorMethodExpression);
        }

        TagAttribute listenerTagAttribute = getRequiredAttribute("listener");
        MethodExpression listenerMethodExpression = listenerTagAttribute.getMethodExpression(context, void.class, new Class[]{
            WebAuthnRegisteredEvent.class
        });
        MethodExpressionAjaxBehaviorListener listener = new MethodExpressionAjaxBehaviorListener(listenerMethodExpression);
        ajaxBehavior.addAjaxBehaviorListener(listener);
        webAuthnComponent.addClientBehavior(WebAuthnRegisteredEvent.NAME, ajaxBehavior);
        LOGGER.debug("added client behavior");
    }
}
