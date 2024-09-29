/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.message;

import java.util.Map;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.PreRenderViewEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import org.primefaces.context.PrimeRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddMessageActionListener implements ActionListener, StateHolder, SystemEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddMessageActionListener.class);

    private boolean _transient;

    private String severity;

    private ValueExpression summary;

    private ValueExpression detail;

    private String target;

    private String whenCallbackParam;

    private String whenCallbackParamValue;

    public AddMessageActionListener() {
        super();
    }

    public AddMessageActionListener(String severity, ValueExpression summary,
            ValueExpression detail, String target, String whenCallbackParam,
            String whenCallbackParamValue) {
        this.severity = severity;
        this.summary = summary;
        this.detail = detail;
        this.target = target;
        this.whenCallbackParam = whenCallbackParam;
        this.whenCallbackParamValue = whenCallbackParamValue;
    }

    @Override
    public Object saveState(final FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }
        return new Object[]{
            this.severity,
            this.summary,
            this.detail,
            this.target,
            this.whenCallbackParam,
            this.whenCallbackParamValue
        };
    }

    @Override
    public void restoreState(final FacesContext context, final Object state) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (state == null) {
            return;
        }
        final Object[] stateObjects = (Object[]) state;
        if (stateObjects.length == 0) {
            return;
        }
        this.severity = (String) stateObjects[0];
        this.summary = (ValueExpression) stateObjects[1];
        this.detail = (ValueExpression) stateObjects[2];
        this.target = (String) stateObjects[3];
        this.whenCallbackParam = (String) stateObjects[4];
        this.whenCallbackParamValue = (String) stateObjects[5];
    }

    @Override
    public boolean isTransient() {
        return this._transient;
    }

    @Override
    public void setTransient(final boolean newTransientValue) {
        this._transient = newTransientValue;
    }

    private String getSummary(FacesContext facesContext) {
        if (null == this.summary) {
            return null;
        }
        ELContext elContext = facesContext.getELContext();
        return (String) this.summary.getValue(elContext);
    }

    private String getDetail(FacesContext facesContext) {
        if (null == this.detail) {
            return null;
        }
        ELContext elContext = facesContext.getELContext();
        return (String) this.detail.getValue(elContext);
    }

    private FacesMessage.Severity getSeverity() {
        if (null == this.severity) {
            return FacesMessage.SEVERITY_INFO;
        }
        switch (this.severity.toUpperCase()) {
            case "INFO":
                return FacesMessage.SEVERITY_INFO;
            case "ERROR":
                return FacesMessage.SEVERITY_ERROR;
            case "FATAL":
                return FacesMessage.SEVERITY_FATAL;
            case "WARN":
                return FacesMessage.SEVERITY_WARN;
            default:
                return FacesMessage.SEVERITY_INFO;
        }
    }

    private String getTargetClientId(ActionEvent event) {
        if (null == this.target) {
            return null;
        }
        UIComponent component = event.getComponent();
        UIComponent targetComponent = component.findComponent(this.target);
        if (null == targetComponent) {
            LOGGER.error("target not found: {}", this.target);
            return null;
        }
        FacesContext facesContext = event.getFacesContext();
        return targetComponent.getClientId(facesContext);
    }

    @Override
    public void processAction(ActionEvent event) throws AbortProcessingException {
        FacesContext facesContext = event.getFacesContext();
        if (null == this.whenCallbackParam) {
            FacesMessage facesMessage = new FacesMessage(getSeverity(), getSummary(facesContext), getDetail(facesContext));
            String clientId = getTargetClientId(event);
            facesContext.addMessage(clientId, facesMessage);
        } else {
            UIViewRoot viewRoot = facesContext.getViewRoot();
            viewRoot.subscribeToViewEvent(PreRenderViewEvent.class, this);
        }
    }

    @Override
    public void processEvent(SystemEvent event) throws AbortProcessingException {
        LOGGER.debug("processEvent: {}", event);
        FacesContext facesContext = event.getFacesContext();
        PrimeRequestContext primeRequestContext = PrimeRequestContext.getCurrentInstance(facesContext);
        Map<String, Object> callbackParams = primeRequestContext.getCallbackParams();
        if (!callbackParams.containsKey(this.whenCallbackParam)) {
            return;
        }
        if (null != this.whenCallbackParamValue) {
            String actualValue = callbackParams.get(this.whenCallbackParam).toString();
            if (!this.whenCallbackParamValue.equals(actualValue)) {
                return;
            }
        }
        FacesMessage facesMessage = new FacesMessage(getSeverity(), getSummary(facesContext), getDetail(facesContext));
        String clientId = null; // TODO
        facesContext.addMessage(clientId, facesMessage);
    }

    @Override
    public boolean isListenerForSource(Object source) {
        LOGGER.debug("source: {}", source);
        return true;
    }
}
