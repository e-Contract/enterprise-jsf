/*
 * Enterprise JSF project.
 *
 * Copyright 2025 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.PartialStateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesValidator(PrefixValidator.VALIDATOR_ID)
public class PrefixValidator implements Validator, PartialStateHolder {

    public static final String VALIDATOR_ID = "ejsf.prefixValidator";

    private static final Logger LOGGER = LoggerFactory.getLogger(PrefixValidator.class);

    private boolean _transient;

    private boolean initialState;

    private String prefix;

    private String message;

    @Override
    public void validate(FacesContext facesContext, UIComponent component, Object value) throws ValidatorException {
        LOGGER.debug("validator prefix: {}", this.prefix);
        String strValue = (String) value;
        if (StringUtils.isEmpty(strValue)) {
            return;
        }

        if (!strValue.startsWith(this.prefix)) {
            String errorMessage;
            if (null != this.message) {
                errorMessage = this.message;
            } else {
                errorMessage = "Expected prefix: " + this.prefix;
            }
            FacesMessage facesMessage
                    = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            errorMessage, null);
            throw new ValidatorException(facesMessage);
        }
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.initialState = false;
        this.prefix = prefix;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.initialState = false;
        this.message = message;
    }

    @Override
    public boolean isTransient() {
        return this._transient;
    }

    @Override
    public void setTransient(boolean newTransientValue) {
        this._transient = newTransientValue;
    }

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

    @Override
    public Object saveState(FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (this.initialState) {
            return null;
        }
        Object state = new Object[]{
            this.prefix,
            this.message
        };
        return state;
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
        this.prefix = (String) stateObjects[0];
        this.message = (String) stateObjects[1];
    }
}
