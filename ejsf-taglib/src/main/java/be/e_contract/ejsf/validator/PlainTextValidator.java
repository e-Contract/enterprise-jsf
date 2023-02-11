/*
 * Enterprise JSF project.
 *
 * Copyright 2022-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator;

import be.e_contract.ejsf.Environment;
import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.StateHolder;
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

@FacesValidator(PlainTextValidator.VALIDATOR_ID)
public class PlainTextValidator implements Validator, StateHolder {

    public static final String VALIDATOR_ID = "ejsf.plainTextValidator";

    private static final Logger LOGGER = LoggerFactory.getLogger(PlainTextValidator.class);

    private boolean _transient;

    private String message;

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void validate(FacesContext facesContext, UIComponent component, Object value) throws ValidatorException {
        if (null == value) {
            return;
        }
        String strValue = (String) value;
        if (UIInput.isEmpty(strValue)) {
            return;
        }
        if (!Environment.hasOwaspHtmlSanitizer()) {
            String errorMessage = "Missing owasp-java-html-sanitizer";
            LOGGER.error(errorMessage);
            FacesMessage facesMessage = new FacesMessage(errorMessage);
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        }
        PolicyFactory policy = new HtmlPolicyBuilder().toFactory();
        String safeHTML = policy.sanitize(strValue);
        if (!safeHTML.equals(strValue)) {
            String errorMessage;
            if (null != this.message) {
                errorMessage = this.message;
            } else {
                Application application = facesContext.getApplication();
                ResourceBundle resourceBundle = application.getResourceBundle(facesContext, "ejsfMessages");
                errorMessage = resourceBundle.getString("invalidCharacters");
            }
            FacesMessage facesMessage = new FacesMessage(errorMessage);
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        }
    }

    @Override
    public Object saveState(FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }
        return new Object[]{
            this.message
        };
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (state == null) {
            return;
        }
        Object[] stateObjects = (Object[]) state;
        if (stateObjects.length == 0) {
            return;
        }
        this.message = (String) stateObjects[0];
    }

    @Override
    public boolean isTransient() {
        return this._transient;
    }

    @Override
    public void setTransient(boolean newTransientValue) {
        this._transient = newTransientValue;
    }
}
