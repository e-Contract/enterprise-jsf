/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator(PINValidator.VALIDATOR_ID)
public class PINValidator implements Validator, StateHolder {

    public static final String VALIDATOR_ID = "ejsf.pinValidator";

    private boolean _transient;

    private String message;

    private Integer minimumDigits;

    private Integer maximumDigits;

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getMinimumDigits() {
        return this.minimumDigits;
    }

    public void setMinimumDigits(Integer minimumDigits) {
        this.minimumDigits = minimumDigits;
    }

    public Integer getMaximumDigits() {
        return this.maximumDigits;
    }

    public void setMaximumDigits(Integer maximumDigits) {
        this.maximumDigits = maximumDigits;
    }

    @Override
    public void validate(FacesContext facesContext, UIComponent component, Object value) throws ValidatorException {
        if (null == value) {
            // allow for optional
            return;
        }
        String pin = (String) value;
        if (pin.isEmpty()) {
            // allow for optional
            return;
        }
        Integer minDigits = getMinimumDigits();
        if (null == minDigits) {
            minDigits = 4;
        }
        Integer maxDigits = getMaximumDigits();
        String regex;
        if (null == maxDigits) {
            regex = "\\d{" + minDigits + ",}";
        } else {
            regex = "\\d{" + minDigits + "," + maxDigits + "}";
        }
        if (!pin.matches(regex)) {
            String errorMessage;
            if (null != this.message) {
                errorMessage = this.message;
            } else {
                errorMessage = "Not a PIN.";
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
            this.message,
            this.minimumDigits,
            this.maximumDigits
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
        this.minimumDigits = (Integer) stateObjects[1];
        this.maximumDigits = (Integer) stateObjects[2];
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
