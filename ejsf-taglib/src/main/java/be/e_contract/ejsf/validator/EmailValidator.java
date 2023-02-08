/*
 * Enterprise JSF project.
 *
 * Copyright 2014-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator;

import java.util.ResourceBundle;
import java.util.StringTokenizer;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesValidator(EmailValidator.VALIDATOR_ID)
public class EmailValidator implements Validator, StateHolder {

    public static final String VALIDATOR_ID = "ejsf.emailValidator";

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailValidator.class);

    private boolean _transient;

    private boolean allowMultiple;

    public boolean isAllowMultiple() {
        return this.allowMultiple;
    }

    public void setAllowMultiple(boolean allowMultiple) {
        this.allowMultiple = allowMultiple;
    }

    @Override
    public void validate(FacesContext facesContext, UIComponent component, Object value) throws ValidatorException {
        LOGGER.debug("validate: {}", value);
        if (null == value) {
            return;
        }
        String strValue = (String) value;
        if (strValue.isEmpty()) {
            return;
        }
        try {
            Class.forName("org.apache.commons.validator.routines.EmailValidator");
        } catch (ClassNotFoundException ex) {
            String errorMessage = "Missing commons-validator";
            LOGGER.error(errorMessage);
            FacesMessage facesMessage = new FacesMessage(errorMessage);
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        }
        org.apache.commons.validator.routines.EmailValidator validator = org.apache.commons.validator.routines.EmailValidator.getInstance();
        boolean valid;
        if (this.allowMultiple) {
            StringTokenizer stringTokenizer = new StringTokenizer(strValue, ",");
            while (stringTokenizer.hasMoreTokens()) {
                String token = stringTokenizer.nextToken();
                valid = validator.isValid(token);
                if (!valid) {
                    break;
                }
            }
            valid = true;
        } else {
            valid = validator.isValid(strValue);
        }

        if (!valid) {
            Application application = facesContext.getApplication();
            ResourceBundle resourceBundle = application.getResourceBundle(facesContext, "ejsfMessages");
            String errorMessage = resourceBundle.getString("invalidEmail");
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
        return new Object[]{this.allowMultiple};
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
        this.allowMultiple = (Boolean) stateObjects[0];
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
