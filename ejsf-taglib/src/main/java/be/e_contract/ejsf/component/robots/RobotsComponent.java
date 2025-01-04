/*
 * Enterprise JSF project.
 *
 * Copyright 2024-2025 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.robots;

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
import javax.faces.event.ComponentSystemEventListener;
import javax.faces.event.ListenerFor;
import javax.faces.event.PostAddToViewEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(RobotsComponent.COMPONENT_TYPE)
@ListenerFor(systemEventClass = PostAddToViewEvent.class)
public class RobotsComponent extends UIComponentBase implements ComponentSystemEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(RobotsComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.robotsComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public RobotsComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        index,
        follow
    }

    public void setIndex(boolean index) {
        getStateHelper().put(PropertyKeys.index, index);
    }

    public boolean isIndex() {
        return (boolean) getStateHelper().eval(PropertyKeys.index, false);
    }

    public void setFollow(boolean follow) {
        getStateHelper().put(PropertyKeys.follow, follow);
    }

    public boolean isFollow() {
        return (boolean) getStateHelper().eval(PropertyKeys.follow, false);
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
                RobotsHeadComponent robotsHeadComponent = (RobotsHeadComponent) application.createComponent(RobotsHeadComponent.COMPONENT_TYPE);
                robotsHeadComponent.setRendered(isRendered());
                robotsHeadComponent.setIndex(isIndex());
                robotsHeadComponent.setFollow(isFollow());
                headComponent.getChildren().add(robotsHeadComponent);
            } else {
                LOGGER.warn("missing h:head on page");
            }
        }
        super.processEvent(event);
    }
}
