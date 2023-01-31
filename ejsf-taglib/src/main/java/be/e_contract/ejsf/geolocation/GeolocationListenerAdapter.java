/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.geolocation;

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeolocationListenerAdapter implements GeolocationEventListener, StateHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeolocationListenerAdapter.class);

    private MethodExpression methodExpression;

    private boolean _transient;

    public GeolocationListenerAdapter() {
        super();
    }

    public GeolocationListenerAdapter(MethodExpression methodExpression) {
        this.methodExpression = methodExpression;
    }

    @Override
    public void processEvent(GeolocationEvent geolocationEvent) {
        LOGGER.debug("processEvent");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext elContext = facesContext.getELContext();
        this.methodExpression.invoke(elContext, new Object[]{geolocationEvent});
    }

    @Override
    public Object saveState(FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }
        return new Object[]{this.methodExpression};
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
        this.methodExpression = (MethodExpression) stateObjects[0];
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
