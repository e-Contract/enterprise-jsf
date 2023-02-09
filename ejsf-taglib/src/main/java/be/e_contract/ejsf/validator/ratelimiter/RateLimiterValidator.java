/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator.ratelimiter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesValidator(RateLimiterValidator.VALIDATOR_ID)
public class RateLimiterValidator implements Validator, StateHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimiterValidator.class);

    private static final String CACHES_ATTRIBUTE = RateLimiterValidator.class.getName() + ".caches";

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
        LOGGER.debug("validate");
        LOGGER.debug("for: {}", this._for);
        String forValue;
        if (null != this.forValueExpression) {
            ELContext elContext = facesContext.getELContext();
            forValue = (String) this.forValueExpression.getValue(elContext);
        } else {
            UIInput forInput = (UIInput) component.findComponent(this._for);
            if (null == forInput) {
                FacesMessage facesMessage = new FacesMessage("Config error.");
                facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(facesMessage);
            }
            forValue = (String) forInput.getValue();
        }
        LOGGER.debug("for value: {}", forValue);
        if (!be.e_contract.ejsf.Runtime.hasCaffeine()) {
            FacesMessage facesMessage = new FacesMessage("Missing caffeine.");
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        }
        boolean reachedLimit = reachedLimit(facesContext, forValue);
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

    private boolean reachedLimit(FacesContext facesContext, String identifier) {
        ExternalContext externalContext = facesContext.getExternalContext();
        ServletContext servletContext = (ServletContext) externalContext.getContext();
        Map<RateExpiry, Cache<String, RateInfo>> caches
                = (Map<RateExpiry, Cache<String, RateInfo>>) servletContext.getAttribute(CACHES_ATTRIBUTE);
        if (null == caches) {
            caches = new ConcurrentHashMap<>();
            servletContext.setAttribute(CACHES_ATTRIBUTE, caches);
        }

        RateExpiry rateExpiry = new RateExpiry(this.limitRefreshPeriod, this.timeoutDuration);
        Cache<String, RateInfo> cache = caches.get(rateExpiry);
        if (null == cache) {
            LOGGER.debug("creating new cache: {}", rateExpiry);
            cache = Caffeine.newBuilder()
                    .expireAfter(rateExpiry)
                    .build();
            caches.put(rateExpiry, cache);
        }
        RateInfo rateInfo = cache.get(identifier, key -> new RateInfo(this.limitForPeriod));
        boolean reachedLimit = rateInfo.reachedLimit();
        if (reachedLimit) {
            // trigger timeoutDuration
            cache.put(identifier, rateInfo);
        }
        return reachedLimit;
    }
}
