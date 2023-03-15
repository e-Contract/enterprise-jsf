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
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator(InetAddressValidator.VALIDATOR_ID)
public class InetAddressValidator implements Validator, StateHolder {

    public static final String VALIDATOR_ID = "ejsf.inetAddressValidator";

    private boolean _transient;

    private String message;

    private boolean ipv4Only;

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIpv4Only() {
        return this.ipv4Only;
    }

    public void setIpv4Only(boolean ipv4Only) {
        this.ipv4Only = ipv4Only;
    }

    @Override
    public void validate(FacesContext facesContext, UIComponent component, Object value) throws ValidatorException {
        if (UIInput.isEmpty(value)) {
            return;
        }
        String strValue = (String) value;
        org.apache.commons.validator.routines.InetAddressValidator validator = new org.apache.commons.validator.routines.InetAddressValidator();
        boolean valid;
        if (this.ipv4Only) {
            valid = validator.isValidInet4Address(strValue);
        } else {
            valid = validator.isValid(strValue);
        }
        if (!valid) {
            String errorMessage;
            if (null != this.message) {
                errorMessage = this.message;
            } else {
                errorMessage = "Invalid internet address.";
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
            this.ipv4Only,
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
        this.ipv4Only = (Boolean) stateObjects[0];
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
