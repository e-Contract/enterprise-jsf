/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator.ratelimiter;

import java.io.IOException;
import javax.faces.application.Application;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.ValidatorConfig;
import javax.faces.view.facelets.ValidatorHandler;

public class RateLimiterValidatorTagHandler extends ValidatorHandler {

    public RateLimiterValidatorTagHandler(ValidatorConfig config) {
        super(config);
    }

    @Override
    public void apply(FaceletContext context, UIComponent parent) throws IOException {
        if (!ComponentHandler.isNew(parent)) {
            return;
        }
        if (!(parent instanceof EditableValueHolder)) {
            super.apply(context, parent);
            return;
        }

        TagAttribute forTagAttribute = getAttribute("for");
        String forValue = forTagAttribute.getValue();

        FacesContext facesContext = context.getFacesContext();
        Application application = facesContext.getApplication();
        RateLimiterValidator rateLimiterValidator = (RateLimiterValidator) application.createValidator(RateLimiterValidator.VALIDATOR_ID);
        rateLimiterValidator.setFor(forValue);

        EditableValueHolder parentEditableValueHolder = (EditableValueHolder) parent;
        parentEditableValueHolder.addValidator(rateLimiterValidator);
    }
}
