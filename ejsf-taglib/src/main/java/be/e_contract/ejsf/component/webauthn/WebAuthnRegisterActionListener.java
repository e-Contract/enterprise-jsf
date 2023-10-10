/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.webauthn;

import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebAuthnRegisterActionListener implements ActionListener, StateHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebAuthnRegisterActionListener.class);

    private boolean _transient;

    private String forAttribute;

    public WebAuthnRegisterActionListener() {
        super();
    }

    public WebAuthnRegisterActionListener(String forAttribute) {
        this.forAttribute = forAttribute;
    }

    @Override
    public void processAction(ActionEvent event) throws AbortProcessingException {
        LOGGER.debug("processAction");
        UIComponent component = event.getComponent();
        WebAuthnComponent webAuthnComponent = (WebAuthnComponent) component.findComponent(this.forAttribute);
        if (null == webAuthnComponent) {
            LOGGER.warn("WebAuthn component not found");
            return;
        }
        FacesContext facesContext = event.getFacesContext();
        String widgetVar = webAuthnComponent.resolveWidgetVar(facesContext);
        PrimeFaces primeFaces = PrimeFaces.current();
        primeFaces.executeScript("PF('" + widgetVar + "').webAuthnRegistration();");
    }

    @Override
    public Object saveState(final FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }
        return new Object[]{
            this.forAttribute
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
        this.forAttribute = (String) stateObjects[0];
    }

    @Override
    public boolean isTransient() {
        return this._transient;
    }

    @Override
    public void setTransient(final boolean newTransientValue) {
        this._transient = newTransientValue;
    }
}
