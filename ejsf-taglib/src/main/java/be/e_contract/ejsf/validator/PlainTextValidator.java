/*
 * Enterprise JSF project.
 *
 * Copyright 2022-2025 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator;

import be.e_contract.ejsf.Environment;
import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.PartialStateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesValidator(PlainTextValidator.VALIDATOR_ID)
public class PlainTextValidator implements Validator, PartialStateHolder {

    public static final String VALIDATOR_ID = "ejsf.plainTextValidator";

    private static final Logger LOGGER = LoggerFactory.getLogger(PlainTextValidator.class);

    private boolean _transient;

    private String message;

    private boolean allowEmail;

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.initialState = false;
        this.message = message;
    }

    public boolean isAllowEmail() {
        return this.allowEmail;
    }

    public void setAllowEmail(boolean allowEmail) {
        this.initialState = false;
        this.allowEmail = allowEmail;
    }

    @Override
    public void validate(FacesContext facesContext, UIComponent component, Object value) throws ValidatorException {
        if (null == value) {
            // allow for optional
            return;
        }
        String strValue = (String) value;
        if (strValue.isEmpty()) {
            // allow for optional
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
        if (this.allowEmail) {
            strValue = strValue.replaceAll("@", "&#64;");
        }
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
        if (this.initialState) {
            return null;
        }
        return new Object[]{
            this.message,
            this.allowEmail
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
        this.allowEmail = (boolean) stateObjects[1];
    }

    @Override
    public boolean isTransient() {
        return this._transient;
    }

    @Override
    public void setTransient(boolean newTransientValue) {
        this._transient = newTransientValue;
    }

    private boolean initialState;

    @Override
    public void markInitialState() {
        this.initialState = true;
    }

    @Override
    public boolean initialStateMarked() {
        return this.initialState;
    }

    @Override
    public void clearInitialState() {
        this.initialState = false;
    }
}
