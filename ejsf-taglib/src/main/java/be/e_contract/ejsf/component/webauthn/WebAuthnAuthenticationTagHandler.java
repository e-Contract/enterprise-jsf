/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.webauthn;

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

public class WebAuthnAuthenticationTagHandler extends TagHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebAuthnAuthenticationTagHandler.class);

    public WebAuthnAuthenticationTagHandler(TagConfig tagConfig) {
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

        TagAttribute listenerTagAttribute = getRequiredAttribute("listener");
        MethodExpression listenerMethodExpression = listenerTagAttribute.getMethodExpression(context, void.class, new Class[]{
            WebAuthnAuthenticatedEvent.class
        });
        MethodExpressionAjaxBehaviorListener listener = new MethodExpressionAjaxBehaviorListener(listenerMethodExpression);
        ajaxBehavior.addAjaxBehaviorListener(listener);
        webAuthnComponent.addClientBehavior(WebAuthnAuthenticatedEvent.NAME, ajaxBehavior);
        LOGGER.debug("added client behavior");
    }
}
