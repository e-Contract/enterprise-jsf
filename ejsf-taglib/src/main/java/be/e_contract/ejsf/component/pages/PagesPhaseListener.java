/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.pages;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PagesPhaseListener implements PhaseListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PagesPhaseListener.class);

    @Override
    public void afterPhase(PhaseEvent event) {
        // empty
    }

    @Override
    public void beforePhase(PhaseEvent event) {
        FacesContext facesContext = event.getFacesContext();
        UIViewRoot viewRoot = facesContext.getViewRoot();
        if (null != viewRoot) {
            String viewId = viewRoot.getViewId();
            PagesComponent.markVisited(viewId);
        }
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }
}
