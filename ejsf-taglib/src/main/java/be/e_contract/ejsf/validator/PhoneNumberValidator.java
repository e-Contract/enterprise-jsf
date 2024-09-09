/*
 * Enterprise JSF project.
 *
 * Copyright 2021-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.PartialStateHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesValidator(PhoneNumberValidator.VALIDATOR_ID)
public class PhoneNumberValidator implements Validator, PartialStateHolder {

    public static final String VALIDATOR_ID = "ejsf.phoneNumberValidator";

    private static final Logger LOGGER = LoggerFactory.getLogger(PhoneNumberValidator.class);

    private boolean _transient;

    private String defaultRegion;

    private String phoneNumberType;

    private String message;

    public String getDefaultRegion() {
        return this.defaultRegion;
    }

    public void setDefaultRegion(String defaultRegion) {
        this.initialState = false;
        this.defaultRegion = defaultRegion;
    }

    public String getPhoneNumberType() {
        return this.phoneNumberType;
    }

    public void setPhoneNumberType(String phoneNumberType) {
        this.initialState = false;
        this.phoneNumberType = phoneNumberType;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.initialState = false;
        this.message = message;
    }

    @Override
    public void validate(FacesContext facesContext, UIComponent component, Object value) throws ValidatorException {
        if (UIInput.isEmpty(value)) {
            // allow for optional
            return;
        }
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber phoneNumber = phoneUtil.parseAndKeepRawInput((String) value, this.defaultRegion);
            boolean validNumber = phoneUtil.isPossibleNumberForType(phoneNumber, PhoneNumberUtil.PhoneNumberType.valueOf(this.phoneNumberType));
            if (!validNumber) {
                Application application = facesContext.getApplication();
                ResourceBundle resourceBundle = application.getResourceBundle(facesContext, "ejsfMessages");
                String errorMessage = resourceBundle.getString("invalidPhoneNumber");
                FacesMessage facesMessage = new FacesMessage(errorMessage);
                facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(facesMessage);
            }
        } catch (NumberParseException ex) {
            LOGGER.debug("invalid phone number: {} : {}", value, ex.getMessage());
            String errorMessage;
            if (null != this.message) {
                errorMessage = this.message;
            } else {
                Application application = facesContext.getApplication();
                ResourceBundle resourceBundle = application.getResourceBundle(facesContext, "ejsfMessages");
                errorMessage = resourceBundle.getString("invalidPhoneNumber");
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
            this.defaultRegion,
            this.phoneNumberType,
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
        this.defaultRegion = (String) stateObjects[0];
        this.phoneNumberType = (String) stateObjects[1];
        this.message = (String) stateObjects[2];
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
