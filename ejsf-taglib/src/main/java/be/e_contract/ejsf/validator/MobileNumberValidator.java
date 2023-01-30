/*
 * Enterprise JSF project.
 *
 * Copyright 2021-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesValidator("ejsf.mobileNumberValidator")
public class MobileNumberValidator implements Validator, StateHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(MobileNumberValidator.class);

    private boolean _transient;

    private String defaultRegion;

    public String getDefaultRegion() {
        return this.defaultRegion;
    }

    public void setDefaultRegion(String defaultRegion) {
        this.defaultRegion = defaultRegion;
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
            boolean validNumber = phoneUtil.isPossibleNumberForType(phoneNumber, PhoneNumberUtil.PhoneNumberType.MOBILE);
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
            Application application = facesContext.getApplication();
            ResourceBundle resourceBundle = application.getResourceBundle(facesContext, "ejsfMessages");
            String errorMessage = resourceBundle.getString("invalidPhoneNumber");
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
        return new Object[]{this.defaultRegion};
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
