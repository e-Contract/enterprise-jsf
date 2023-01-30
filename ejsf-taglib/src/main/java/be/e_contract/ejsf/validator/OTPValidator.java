/*
 * Enterprise JSF project.
 *
 * Copyright 2021-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator;

import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("ejsf.otpValidator")
public class OTPValidator implements Validator {

    @Override
    public void validate(FacesContext facesContext, UIComponent component, Object value) throws ValidatorException {
        if (UIInput.isEmpty(value)) {
            // allow for optional
            return;
        }
        String otp = (String) value;
        if (!otp.matches("\\d{6}")) {
            Application application = facesContext.getApplication();
            ResourceBundle resourceBundle = application.getResourceBundle(facesContext, "ejsfMessages");
            String errorMessage = resourceBundle.getString("invalidOTP");
            FacesMessage facesMessage = new FacesMessage(errorMessage);
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        }
    }
}
