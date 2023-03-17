/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator.keystore;

import be.e_contract.ejsf.validator.ratelimiter.RateLimiterValidator;
import java.io.IOException;
import javax.faces.application.Application;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagException;
import javax.faces.view.facelets.ValidatorConfig;
import javax.faces.view.facelets.ValidatorHandler;

public class KeyStorePasswordValidatorTagHandler extends ValidatorHandler {

    public KeyStorePasswordValidatorTagHandler(ValidatorConfig config) {
        super(config);
    }

    @Override
    public void apply(FaceletContext context, UIComponent parent) throws IOException {
        if (!ComponentHandler.isNew(parent)) {
            return;
        }
        if (!(parent instanceof EditableValueHolder)) {
            throw new TagException(this.tag, "parent must be EditableValueHolder");
        }
        TagAttribute forKeyStoreTagAttribute = getRequiredAttribute("forKeyStore");
        String forKeyStore = forKeyStoreTagAttribute.getValue(context);
        TagAttribute keyStoreTypeTagAttribute = getRequiredAttribute("keyStoreType");
        String keyStoreType = keyStoreTypeTagAttribute.getValue(context);
        String message;
        TagAttribute messageTagAttribute = getAttribute("message");
        if (null != messageTagAttribute) {
            message = messageTagAttribute.getValue(context);
        } else {
            message = null;
        }

        FacesContext facesContext = context.getFacesContext();
        Application application = facesContext.getApplication();

        RateLimiterValidator rateLimiterValidator = (RateLimiterValidator) application.createValidator(RateLimiterValidator.VALIDATOR_ID);
        rateLimiterValidator.setFor(forKeyStore);
        rateLimiterValidator.setTimeoutDuration(30);
        rateLimiterValidator.setLimitRefreshPeriod(10);
        rateLimiterValidator.setLimitForPeriod(5);
        EditableValueHolder parentEditableValueHolder = (EditableValueHolder) parent;
        parentEditableValueHolder.addValidator(rateLimiterValidator);

        KeyStorePasswordValidator keyStorePasswordValidator = (KeyStorePasswordValidator) application.createValidator(KeyStorePasswordValidator.VALIDATOR_ID);
        keyStorePasswordValidator.setKeyStoreType(keyStoreType);
        keyStorePasswordValidator.setForKeyStore(forKeyStore);
        keyStorePasswordValidator.setMessage(message);
        parentEditableValueHolder.addValidator(keyStorePasswordValidator);
    }
}
