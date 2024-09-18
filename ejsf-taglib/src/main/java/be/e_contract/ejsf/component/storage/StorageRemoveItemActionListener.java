/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.storage;

import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageRemoveItemActionListener implements ActionListener, StateHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageRemoveItemActionListener.class);

    private boolean _transient;

    private String name;

    private String type;

    public StorageRemoveItemActionListener() {
        super();
    }

    public StorageRemoveItemActionListener(String type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public Object saveState(final FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }
        return new Object[]{
            this.type,
            this.name
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
        this.type = (String) stateObjects[0];
        this.name = (String) stateObjects[1];
    }

    @Override
    public boolean isTransient() {
        return this._transient;
    }

    @Override
    public void setTransient(final boolean newTransientValue) {
        this._transient = newTransientValue;
    }

    @Override
    public void processAction(ActionEvent event) throws AbortProcessingException {
        FacesContext facesContext = event.getFacesContext();
        PartialViewContext partialViewContext = facesContext.getPartialViewContext();
        String script = "ejsf.storageRemoveItem('" + this.name + "','" + this.type + "');";
        partialViewContext.getEvalScripts().add(script);
    }
}