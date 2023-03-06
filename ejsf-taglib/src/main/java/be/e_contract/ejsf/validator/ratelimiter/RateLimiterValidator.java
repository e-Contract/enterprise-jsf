/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator.ratelimiter;

import be.e_contract.ejsf.Environment;
import java.util.ResourceBundle;
import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesValidator(RateLimiterValidator.VALIDATOR_ID)
public class RateLimiterValidator implements Validator, StateHolder, ActionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimiterValidator.class);

    public static final String VALIDATOR_ID = "ejsf.rateLimiterValidator";

    private String _for;

    private int timeoutDuration;

    private int limitRefreshPeriod;

    private int limitForPeriod;

    private ValueExpression messageValueExpression;

    private MethodExpression onLimitMethodExpression;

    private ValueExpression forValueExpression;

    private boolean _transient;

    public String getFor() {
        return this._for;
    }

    public void setFor(String _for) {
        this._for = _for;
    }

    public int getTimeoutDuration() {
        return this.timeoutDuration;
    }

    public void setTimeoutDuration(int timeoutDuration) {
        this.timeoutDuration = timeoutDuration;
    }

    public int getLimitRefreshPeriod() {
        return this.limitRefreshPeriod;
    }

    public void setLimitRefreshPeriod(int limitRefreshPeriod) {
        this.limitRefreshPeriod = limitRefreshPeriod;
    }

    public int getLimitForPeriod() {
        return this.limitForPeriod;
    }

    public void setLimitForPeriod(int limitForPeriod) {
        this.limitForPeriod = limitForPeriod;
    }

    public ValueExpression getMessageValueExpression() {
        return this.messageValueExpression;
    }

    public void setMessageValueExpression(ValueExpression messageValueExpression) {
        this.messageValueExpression = messageValueExpression;
    }

    public MethodExpression getOnLimitMethodExpression() {
        return this.onLimitMethodExpression;
    }

    public void setOnLimitMethodExpression(MethodExpression onLimitMethodExpression) {
        this.onLimitMethodExpression = onLimitMethodExpression;
    }

    public ValueExpression getForValueExpression() {
        return this.forValueExpression;
    }

    public void setForValueExpression(ValueExpression forValueExpression) {
        this.forValueExpression = forValueExpression;
    }

    @Override
    public void validate(FacesContext facesContext, UIComponent component, Object value) throws ValidatorException {
        String forValue;
        if (null != this.forValueExpression) {
            ELContext elContext = facesContext.getELContext();
            forValue = (String) this.forValueExpression.getValue(elContext);
        } else {
            UIInput forInput = (UIInput) component.findComponent(this._for);
            if (null == forInput) {
                LOGGER.error("component not found: {}", this._for);
                FacesMessage facesMessage = new FacesMessage("Config error.");
                facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(facesMessage);
            }
            forValue = (String) forInput.getValue();
        }
        if (UIInput.isEmpty(forValue)) {
            LOGGER.warn("for value is empty");
            return;
        }
        LOGGER.debug("for value: {}", forValue);
        if (!Environment.hasCaffeine()) {
            String errorMessage = "Missing caffeine.";
            LOGGER.error(errorMessage);
            FacesMessage facesMessage = new FacesMessage(errorMessage);
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        }
        RateLimiter rateLimiter = new RateLimiter(this.limitRefreshPeriod, this.limitForPeriod, this.timeoutDuration);
        boolean reachedLimit = rateLimiter.reachedLimit(facesContext, forValue);
        if (reachedLimit) {
            if (null != this.onLimitMethodExpression) {
                ELContext elContext = facesContext.getELContext();
                this.onLimitMethodExpression.invoke(elContext, new Object[]{forValue});
            }
            String message;
            if (null != this.messageValueExpression) {
                ELContext elContext = facesContext.getELContext();
                message = (String) this.messageValueExpression.getValue(elContext);
            } else {
                Application application = facesContext.getApplication();
                ResourceBundle resourceBundle = application.getResourceBundle(facesContext, "ejsfMessages");
                message = resourceBundle.getString("rateLimiter");
            }
            LOGGER.debug("reached limit for {}: {}", forValue, message);
            FacesMessage facesMessage = new FacesMessage(message);
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
            this._for,
            this.timeoutDuration,
            this.limitRefreshPeriod,
            this.limitForPeriod,
            this.messageValueExpression,
            this.onLimitMethodExpression,
            this.forValueExpression
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
        this._for = (String) stateObjects[0];
        this.timeoutDuration = (Integer) stateObjects[1];
        this.limitRefreshPeriod = (Integer) stateObjects[2];
        this.limitForPeriod = (Integer) stateObjects[3];
        this.messageValueExpression = (ValueExpression) stateObjects[4];
        this.onLimitMethodExpression = (MethodExpression) stateObjects[5];
        this.forValueExpression = (ValueExpression) stateObjects[6];
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
    public void processAction(ActionEvent event) throws AbortProcessingException {
        UIComponent component = event.getComponent();
        FacesContext facesContext = event.getFacesContext();
        try {
            validate(facesContext, component, null);
        } catch (ValidatorException ex) {
            throw new AbortProcessingException(ex);
        }
    }
}
