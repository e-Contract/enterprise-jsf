/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator.el;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesValidator(ELValidator.VALIDATOR_ID)
public class ELValidator implements Validator, StateHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ELValidator.class);

    public static final String VALIDATOR_ID = "ejsf.elValidator";

    private boolean _transient;

    private String message;

    private String invalidWhen;

    private String valueVar;

    private String prevRowVar;

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getInvalidWhen() {
        return this.invalidWhen;
    }

    public void setInvalidWhen(String invalidWhen) {
        this.invalidWhen = invalidWhen;
    }

    public String getValueVar() {
        return this.valueVar;
    }

    public void setValueVar(String valueVar) {
        this.valueVar = valueVar;
    }

    public String getPrevRowVar() {
        return this.prevRowVar;
    }

    public void setPrevRowVar(String prevRowVar) {
        this.prevRowVar = prevRowVar;
    }

    /**
     * Keep track of the previous row on a per-validator basis.
     */
    private Object prevRow;

    @Override
    public void validate(FacesContext facesContext, UIComponent component, Object value) throws ValidatorException {
        if (null == value) {
            // allow for optional
            return;
        }
        ELContext elContext = facesContext.getELContext();
        Application application = facesContext.getApplication();
        ExpressionFactory expressionFactory = application.getExpressionFactory();
        ELResolver elResolver = elContext.getELResolver();
        elResolver.setValue(elContext, null, this.valueVar, value);
        if (this.prevRowVar != null) {
            String rowVar = findRowVar(component);
            if (null != rowVar) {
                Object currentRow = elResolver.getValue(elContext, null, rowVar);
                LOGGER.debug("current row: {}", currentRow);
                elResolver.setValue(elContext, null, this.prevRowVar, this.prevRow);
                this.prevRow = currentRow;
            }
        }
        ValueExpression valueExpression = expressionFactory.createValueExpression(elContext, this.invalidWhen, Boolean.class);
        Boolean invalid;
        try {
            invalid = (Boolean) valueExpression.getValue(elContext);
        } catch (ELException ex) {
            LOGGER.warn("EL error: " + ex.getMessage(), ex);
            FacesMessage facesMessage = new FacesMessage(ex.getMessage());
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        }
        LOGGER.debug("{} => {}", this.invalidWhen, invalid);
        if (invalid) {
            FacesMessage facesMessage = new FacesMessage(this.message);
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        }
    }

    private String findRowVar(UIComponent component) {
        UIComponent parent = component.getParent();
        while (parent != null) {
            if (parent instanceof UIData) {
                UIData uiData = (UIData) parent;
                return uiData.getVar();
            }
            parent = parent.getParent();
        }
        return null;
    }

    @Override
    public Object saveState(FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }
        return new Object[]{
            this.valueVar,
            this.invalidWhen,
            this.message,
            this.prevRowVar
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
        this.valueVar = (String) stateObjects[0];
        this.invalidWhen = (String) stateObjects[1];
        this.message = (String) stateObjects[2];
        this.prevRowVar = (String) stateObjects[3];
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
