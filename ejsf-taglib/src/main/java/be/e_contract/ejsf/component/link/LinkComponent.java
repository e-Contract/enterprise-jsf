/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.link;

import javax.el.ValueExpression;
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

@FacesComponent(LinkComponent.COMPONENT_TYPE)
@ListenerFor(systemEventClass = PostAddToViewEvent.class)
public class LinkComponent extends UIComponentBase implements ComponentSystemEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.linkComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public LinkComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
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

    enum PropertyKeys {
        href,
        rel
    }

    public void setHref(ValueExpression href) {
        getStateHelper().put(PropertyKeys.href, href);
    }

    public ValueExpression getHref() {
        return (ValueExpression) getStateHelper().eval(PropertyKeys.href);
    }

    public void setRel(String rel) {
        getStateHelper().put(PropertyKeys.rel, rel);
    }

    public String getRel() {
        return (String) getStateHelper().eval(PropertyKeys.rel);
    }

    @Override
    public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
        LOGGER.debug("processEvent");
        if (event instanceof PostAddToViewEvent) {
            FacesContext facesContext = event.getFacesContext();
            UIComponent headComponent = findHead(facesContext);
            if (null != headComponent) {
                Application application = facesContext.getApplication();
                LinkHeadComponent linkHeadComponent = (LinkHeadComponent) application.createComponent(LinkHeadComponent.COMPONENT_TYPE);
                linkHeadComponent.setRendered(isRendered());
                linkHeadComponent.setHref(getHref());
                linkHeadComponent.setRel(getRel());
                headComponent.getChildren().add(linkHeadComponent);
            } else {
                LOGGER.warn("missing h:head on page");
            }
            return;
        }
        super.processEvent(event);
    }
}
