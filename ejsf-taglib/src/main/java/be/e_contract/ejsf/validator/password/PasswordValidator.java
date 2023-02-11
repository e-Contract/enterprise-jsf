/*
 * Enterprise JSF project.
 *
 * Copyright 2021-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator.password;

import be.e_contract.ejsf.Environment;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javax.faces.component.UIInput;
import org.passay.RuleResult;
import org.passay.RuleResultDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesValidator(PasswordValidator.VALIDATOR_ID)
public class PasswordValidator implements Validator<String> {

    public static final String VALIDATOR_ID = "ejsf.passwordValidator";

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordValidator.class);

    @Override
    public void validate(FacesContext facesContext, UIComponent component, String value) throws ValidatorException {
        if (UIInput.isEmpty(value)) {
            return;
        }
        if (!Environment.hasPassay()) {
            String errorMessage = "Missing passay";
            LOGGER.error(errorMessage);
            FacesMessage facesMessage = new FacesMessage(errorMessage);
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        }
        RuleResult result = PassayPasswordValidator.validate(value);
        if (result.isValid()) {
            return;
        }
        LOGGER.debug("password validator result invalid");
        Application application = facesContext.getApplication();
        ResourceBundle resourceBundle = application.getResourceBundle(facesContext, "ejsfMessages");
        throw new ValidatorException(result.getDetails()
                .stream()
                .map((RuleResultDetail ruleResultDetail) -> {
                    String errorMessage = resourceBundle.getString("password."
                            + ruleResultDetail.getErrorCode());
                    FacesMessage facesMessage = new FacesMessage(errorMessage);
                    facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
                    return facesMessage;
                })
                .collect(Collectors.toList()));
    }
}
