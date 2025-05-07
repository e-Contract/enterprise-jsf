/*
 * Enterprise JSF project.
 *
 * Copyright 2025 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.refresh;

import javax.faces.application.Application;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIOutput;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlBody;
import javax.faces.component.html.HtmlHead;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ListenerFor;
import javax.faces.event.PostAddToViewEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(RefreshComponent.COMPONENT_TYPE)
@ListenerFor(systemEventClass = PostAddToViewEvent.class)
public class RefreshComponent extends UIComponentBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(RefreshComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.refreshComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public RefreshComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        delay,
        viewId
    }

    public void setDelay(int delay) {
        getStateHelper().put(PropertyKeys.delay, delay);
    }

    public int getDelay() {
        return (int) getStateHelper().eval(PropertyKeys.delay, 5);
    }

    public void setViewId(String viewId) {
        getStateHelper().put(PropertyKeys.viewId, viewId);
    }

    public String getViewId() {
        return (String) getStateHelper().eval(PropertyKeys.viewId);
    }

    private UIComponent findHead(FacesContext facesContext) {
        UIViewRoot viewRoot = facesContext.getViewRoot();
        for (UIComponent component : viewRoot.getChildren()) {
            if (component instanceof HtmlHead) {
                return (HtmlHead) component;
            }
        }
        for (UIComponent component : viewRoot.getChildren()) {
            if (component instanceof HtmlBody) {
                return null;
            }
            if (component instanceof UIOutput) {
                if (component.getFacets() != null) {
                    return component;
                }
            }
        }
        return null;
    }

    @Override
    public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
        if (event instanceof PostAddToViewEvent) {
            FacesContext facesContext = event.getFacesContext();
            UIComponent headComponent = findHead(facesContext);
            if (null != headComponent) {
                Application application = facesContext.getApplication();
                RefreshHeadComponent refreshHeadComponent = (RefreshHeadComponent) application.createComponent(RefreshHeadComponent.COMPONENT_TYPE);
                refreshHeadComponent.setRendered(isRendered());
                refreshHeadComponent.setDelay(getDelay());
                refreshHeadComponent.setViewId(getViewId());
                headComponent.getChildren().add(refreshHeadComponent);
            } else {
                LOGGER.warn("missing h:head on page");
            }
        }
        super.processEvent(event);
    }
}
