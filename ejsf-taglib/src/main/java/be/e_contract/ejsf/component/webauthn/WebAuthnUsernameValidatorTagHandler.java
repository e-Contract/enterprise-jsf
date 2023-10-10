/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.webauthn;

import com.yubico.webauthn.CredentialRepository;
import java.io.IOException;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagException;
import javax.faces.view.facelets.TagHandler;
import javax.faces.view.facelets.ValidatorConfig;

public class WebAuthnUsernameValidatorTagHandler extends TagHandler {

    public WebAuthnUsernameValidatorTagHandler(TagConfig tagConfig) {
        super(tagConfig);
    }

    public WebAuthnUsernameValidatorTagHandler(ValidatorConfig validatorConfig) {
        super(validatorConfig);
    }

    @Override
    public void apply(FaceletContext context, UIComponent parent) throws IOException {
        if (!ComponentHandler.isNew(parent)) {
            return;
        }
        if (!(parent instanceof EditableValueHolder)) {
            throw new TagException(this.tag, "parent must be EditableValueHolder");
        }

        TagAttribute modeTagAttribute = getRequiredAttribute("mode");
        String mode = modeTagAttribute.getValue();

        ValueExpression messageValueExpression;
        TagAttribute messageTagAttribute = getAttribute("message");
        if (null == messageTagAttribute) {
            messageValueExpression = null;
        } else {
            messageValueExpression = messageTagAttribute.getValueExpression(context, String.class);
        }

        TagAttribute credentialRepositoryTagAttribute = getRequiredAttribute("credentialRepository");
        ValueExpression credentialRepository = credentialRepositoryTagAttribute.getValueExpression(context, CredentialRepository.class);

        EditableValueHolder editableValueHolder = (EditableValueHolder) parent;
        FacesContext facesContext = context.getFacesContext();
        Application application = facesContext.getApplication();
        WebAuthnUsernameValidator webAuthnUsernameValidator = (WebAuthnUsernameValidator) application.createValidator(WebAuthnUsernameValidator.VALIDATOR_ID);
        webAuthnUsernameValidator.setMessage(messageValueExpression);
        webAuthnUsernameValidator.setMode(mode);
        webAuthnUsernameValidator.setCredentialRepository(credentialRepository);
        editableValueHolder.addValidator(webAuthnUsernameValidator);
    }
}
