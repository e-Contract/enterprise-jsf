/*
 * Enterprise JSF project.
 *
 * Copyright 2023-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator;

import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import javax.faces.application.FacesMessage;
import javax.faces.component.PartialStateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesValidator(CronValidator.VALIDATOR_ID)
public class CronValidator implements Validator, PartialStateHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(CronValidator.class);

    public static final String VALIDATOR_ID = "ejsf.cronValidator";

    private boolean _transient;

    private String cronType;

    private String message;

    public String getCronType() {
        return this.cronType;
    }

    public void setCronType(String cronType) {
        this.initialState = false;
        this.cronType = cronType;
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
        if (null == value) {
            // allow for optional
            return;
        }
        String cronExpression = (String) value;
        if (cronExpression.isEmpty()) {
            // allow for optional
            return;
        }
        CronType cronType;
        try {
            if (null == this.cronType) {
                throw new IllegalArgumentException("missing cronType");
            }
            cronType = CronType.valueOf(this.cronType);
        } catch (IllegalArgumentException ex) {
            LOGGER.error("configuration error on cronType: {}", this.cronType);
            FacesMessage facesMessage = new FacesMessage("Configuration error.");
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        } catch (NoClassDefFoundError e) {
            LOGGER.error("class not found: " + e.getMessage(), e);
            FacesMessage facesMessage = new FacesMessage("Configuration error.");
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        }
        CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(cronType);
        CronParser cronParser = new CronParser(cronDefinition);
        try {
            cronParser.parse(cronExpression);
        } catch (IllegalArgumentException ex) {
            LOGGER.debug("Cron error: {}", ex.getMessage());
            String errorMessage;
            if (null != this.message) {
                errorMessage = this.message;
            } else {
                errorMessage = "Not a valid cron expression.";
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
            this.cronType,
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
        this.cronType = (String) stateObjects[0];
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
