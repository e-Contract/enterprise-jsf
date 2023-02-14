/*
 * Enterprise JSF project.
 *
 * Copyright 2021-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;
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

@FacesValidator(AgeValidator.VALIDATOR_ID)
public class AgeValidator implements Validator, StateHolder {

    public static final String VALIDATOR_ID = "ejsf.ageValidator";

    private static final Logger LOGGER = LoggerFactory.getLogger(AgeValidator.class);

    private boolean _transient;

    private int minimumAge;

    private String message;

    public void setMinimumAge(int minimumAge) {
        this.minimumAge = minimumAge;
    }

    public int getMinimumAge() {
        return this.minimumAge;
    }

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
        LOGGER.debug("value: {}", value);
        LOGGER.debug("value type: {}", value.getClass().getName());
        int age;
        if (value instanceof LocalDate) {
            LocalDate dateOfBirth = (LocalDate) value;
            LocalDate now = LocalDate.now();
            age = Period.between(dateOfBirth, now).getYears();
        } else if (value instanceof Date) {
            LocalDate dateOfBirth = ((Date) value).toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            LocalDate now = LocalDate.now();
            age = Period.between(dateOfBirth, now).getYears();
        } else {
            String errorMessage = "unsupported type: " + value.getClass().getName();
            LOGGER.error(errorMessage);
            FacesMessage facesMessage = new FacesMessage(errorMessage);
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        }
        if (age < this.minimumAge) {
            String errorMessage;
            if (null != this.message) {
                errorMessage = this.message;
            } else {
                Application application = facesContext.getApplication();
                ResourceBundle resourceBundle = application.getResourceBundle(facesContext, "ejsfMessages");
                errorMessage = MessageFormat.format(resourceBundle.getString("minimumAge"), this.minimumAge);
            }
            FacesMessage facesMessage = new FacesMessage(errorMessage);
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        }
    }

    @Override
    public Object saveState(FacesContext context) {
        LOGGER.debug("saveState");
        if (context == null) {
            throw new NullPointerException();
        }
        return new Object[]{
            this.minimumAge,
            this.message
        };
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        LOGGER.debug("restoreState");
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
        this.minimumAge = (Integer) stateObjects[0];
        this.message = (String) stateObjects[1];
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
