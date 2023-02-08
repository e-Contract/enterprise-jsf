/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator.ratelimiter;

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

@FacesValidator(RateLimiterValidator.VALIDATOR_ID)
public class RateLimiterValidator implements Validator, StateHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimiterValidator.class);

    public static final String VALIDATOR_ID = "ejsf.rateLimiterValidator";

    private String _for;

    private boolean _transient;

    public String getFor() {
        return this._for;
    }

    public void setFor(String _for) {
        this._for = _for;
    }

    @Override
    public void validate(FacesContext facesContext, UIComponent component, Object value) throws ValidatorException {
        LOGGER.debug("validate");
        LOGGER.debug("for: {}", this._for);
        UIInput forInput = (UIInput) component.findComponent(this._for);
        if (null == forInput) {
            FacesMessage facesMessage = new FacesMessage("Config error.");
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        }
        String forValue = (String) forInput.getValue();
        LOGGER.debug("for value: {}", forValue);
        if (!be.e_contract.ejsf.Runtime.hasCaffeine()) {
            FacesMessage facesMessage = new FacesMessage("Missing caffeine.");
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        }
        boolean reachedLimit = RateKeeper.reachedLimit(facesContext, forValue);
        if (reachedLimit) {
            FacesMessage facesMessage = new FacesMessage("Please try again later.");
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        }
    }

    @Override
    public Object saveState(FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }
        return new Object[]{this._for};
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
        this._for = (String) stateObjects[0];
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
