/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.announcement;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

public class AcceptAnnouncementActionListener implements ActionListener, StateHolder {

    private ValueExpression retentionValueExpression;

    private boolean _transient;

    public AcceptAnnouncementActionListener() {
        super();
    }

    public AcceptAnnouncementActionListener(ValueExpression retentionValueExpression) {
        this.retentionValueExpression = retentionValueExpression;
    }

    @Override
    public void processAction(ActionEvent actionEvent) throws AbortProcessingException {
        UIComponent component = actionEvent.getComponent();
        AnnouncementComponent announcementComponent = findAnnouncementComponent(component);
        if (null == announcementComponent) {
            return;
        }

        FacesContext facesContext = actionEvent.getFacesContext();
        ELContext elContext = facesContext.getELContext();
        int retention = (int) this.retentionValueExpression.getValue(elContext);

        announcementComponent.acceptAnnouncement(retention);

        PartialViewContext partialViewContext = facesContext.getPartialViewContext();
        String clientId = announcementComponent.getClientId();
        partialViewContext.getRenderIds().add(clientId);
    }

    private AnnouncementComponent findAnnouncementComponent(UIComponent component) {
        while (component != null) {
            if (component instanceof AnnouncementComponent) {
                return (AnnouncementComponent) component;
            }
            component = component.getParent();
        }
        return null;
    }

    @Override
    public Object saveState(final FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }
        return new Object[]{
            this.retentionValueExpression
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
        this.retentionValueExpression = (ValueExpression) stateObjects[0];
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
