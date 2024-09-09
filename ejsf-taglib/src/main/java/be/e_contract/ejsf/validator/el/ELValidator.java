/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator.el;

import java.util.List;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.PartialStateHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesValidator(ELValidator.VALIDATOR_ID)
public class ELValidator implements Validator, PartialStateHolder {

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
        this.initialState = false;
        this.message = message;
    }

    public String getInvalidWhen() {
        return this.invalidWhen;
    }

    public void setInvalidWhen(String invalidWhen) {
        this.initialState = false;
        this.invalidWhen = invalidWhen;
    }

    public String getValueVar() {
        return this.valueVar;
    }

    public void setValueVar(String valueVar) {
        this.initialState = false;
        this.valueVar = valueVar;
    }

    public String getPrevRowVar() {
        return this.prevRowVar;
    }

    public void setPrevRowVar(String prevRowVar) {
        this.initialState = false;
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
            UIData uiData = findUIData(component);
            if (null != uiData) {
                Object currentRow = uiData.getRowData();
                if (null != this.prevRow) {
                    elResolver.setValue(elContext, null, this.prevRowVar, this.prevRow);
                } else {
                    int rowIndex = uiData.getRowIndex();
                    if (rowIndex > 0) {
                        Object data = uiData.getValue();
                        if (data instanceof List) {
                            List dataList = (List) data;
                            Object prevRow = dataList.get(rowIndex - 1);
                            elResolver.setValue(elContext, null, this.prevRowVar, prevRow);
                        } else {
                            LOGGER.error("unsupported data type: {}", data.getClass().getName());
                            elResolver.setValue(elContext, null, this.prevRowVar, null);
                        }
                    } else {
                        elResolver.setValue(elContext, null, this.prevRowVar, null);
                    }
                }
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
            ValueExpression messageValueExpression = expressionFactory.createValueExpression(elContext, this.message, String.class);
            String resultMessage = (String) messageValueExpression.getValue(elContext);
            FacesMessage facesMessage = new FacesMessage(resultMessage);
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        }
    }

    private UIData findUIData(UIComponent component) {
        UIComponent parent = component.getParent();
        while (parent != null) {
            if (parent instanceof UIData) {
                UIData uiData = (UIData) parent;
                return uiData;
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
        if (this.initialState) {
            return null;
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
