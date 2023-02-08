/*
 * Enterprise JSF project.
 *
 * Copyright 2022-2023 e-Contract.be BV. All rights reserved.
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
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesValidator("ejsf.plainTextValidator")
public class PlainTextValidator implements Validator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlainTextValidator.class);

    @Override
    public void validate(FacesContext facesContext, UIComponent component, Object value) throws ValidatorException {
        if (null == value) {
            return;
        }
        String strValue = (String) value;
        if (UIInput.isEmpty(strValue)) {
            return;
        }
        try {
            Class.forName("org.owasp.html.HtmlPolicyBuilder");
        } catch (ClassNotFoundException ex) {
            String errorMessage = "Missing owasp-java-html-sanitizer";
            LOGGER.error(errorMessage);
            FacesMessage facesMessage = new FacesMessage(errorMessage);
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        }
        PolicyFactory policy = new HtmlPolicyBuilder().toFactory();
        String safeHTML = policy.sanitize(strValue);
        if (!safeHTML.equals(strValue)) {
            Application application = facesContext.getApplication();
            ResourceBundle resourceBundle = application.getResourceBundle(facesContext, "ejsfMessages");
            String errorMessage = resourceBundle.getString("invalidCharacters");
            FacesMessage facesMessage = new FacesMessage(errorMessage);
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        }
    }
}
