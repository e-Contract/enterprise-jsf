/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator.el;

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

public class ELValidatorTagHandler extends ValidatorHandler {

    public ELValidatorTagHandler(ValidatorConfig config) {
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
        EditableValueHolder parentEditableValueHolder = (EditableValueHolder) parent;
        FacesContext facesContext = context.getFacesContext();
        Application application = facesContext.getApplication();
        ELValidator elValidator = (ELValidator) application.createValidator(ELValidator.VALIDATOR_ID);

        TagAttribute valueVarTagAttribute = getRequiredAttribute("valueVar");
        String valueVar = valueVarTagAttribute.getValue(context);
        elValidator.setValueVar(valueVar);

        TagAttribute invalidWhenTagAttribute = getRequiredAttribute("invalidWhen");
        String invalidWhen = invalidWhenTagAttribute.getValue();
        elValidator.setInvalidWhen(invalidWhen);

        TagAttribute messageTagAttribute = getRequiredAttribute("message");
        String message = messageTagAttribute.getValue();
        elValidator.setMessage(message);

        TagAttribute prevRowVarTagAttribute = getAttribute("prevRowVar");
        if (null != prevRowVarTagAttribute) {
            String prevRowVar = prevRowVarTagAttribute.getValue(context);
            elValidator.setPrevRowVar(prevRowVar);
        }

        parentEditableValueHolder.addValidator(elValidator);
    }
}
