/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.linkeddata;

import java.io.IOException;
import javax.faces.application.Application;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIOutput;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlBody;
import javax.faces.component.html.HtmlHead;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ComponentSystemEventListener;
import javax.faces.event.ListenerFor;
import javax.faces.event.ListenersFor;
import javax.faces.event.PostAddToViewEvent;
import javax.faces.event.PreRenderViewEvent;
import javax.faces.render.RenderKit;
import org.primefaces.renderkit.RendererUtils;
import org.primefaces.util.FastStringWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(LinkedDataComponent.COMPONENT_TYPE)
@ListenersFor({
    @ListenerFor(systemEventClass = PostAddToViewEvent.class)
})
public class LinkedDataComponent extends UIComponentBase implements ComponentSystemEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkedDataComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.linkedDataComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public LinkedDataComponent() {
        setRendererType(null);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UIViewRoot viewRoot = facesContext.getViewRoot();
        viewRoot.subscribeToEvent(PreRenderViewEvent.class, this);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        value,
    }

    public void setValue(String value) {
        getStateHelper().put(PropertyKeys.value, value);
    }

    public String getValue() {
        return (String) getStateHelper().eval(PropertyKeys.value);
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
            if (null == headComponent) {
                LOGGER.warn("missing h:head on page");
                return;
            }
            for (UIComponent headChild : headComponent.getChildren()) {
                if (headChild instanceof LinkedDataHeadComponent) {
                    return;
                }
            }
            Application application = facesContext.getApplication();
            LinkedDataHeadComponent linkedDataHeadComponent = (LinkedDataHeadComponent) application.createComponent(LinkedDataHeadComponent.COMPONENT_TYPE);
            headComponent.getChildren().add(linkedDataHeadComponent);
            return;
        }
        if (event instanceof PreRenderViewEvent) {
            FacesContext facesContext = event.getFacesContext();
            UIComponent headComponent = findHead(facesContext);
            if (null == headComponent) {
                LOGGER.warn("missing h:head on page");
                return;
            }
            for (UIComponent headChild : headComponent.getChildren()) {
                if (!(headChild instanceof LinkedDataHeadComponent)) {
                    continue;
                }
                LinkedDataHeadComponent linkedDataHeadComponent = (LinkedDataHeadComponent) headChild;
                String value = getValue();
                if (null != value) {
                    linkedDataHeadComponent.addContent(value);
                } else {
                    FastStringWriter output = new FastStringWriter();
                    ResponseWriter originalResponseWriter = facesContext.getResponseWriter();
                    if (null != originalResponseWriter) {
                        facesContext.setResponseWriter(originalResponseWriter.cloneWithWriter(output));
                    } else {
                        RenderKit renderKit = RendererUtils.getRenderKit(facesContext);
                        facesContext.setResponseWriter(renderKit.createResponseWriter(output, "text/html", "UTF-8"));
                    }
                    try {
                        for (UIComponent child : getChildren()) {
                            child.encodeAll(facesContext);
                        }
                    } catch (IOException ex) {
                        LOGGER.error("I/O error: " + ex.getMessage(), ex);
                    } finally {
                        if (null != originalResponseWriter) {
                            facesContext.setResponseWriter(originalResponseWriter);
                        }
                    }
                    linkedDataHeadComponent.addContent(output.toString());
                }
                return;
            }
            LOGGER.warn("no LinkedDataHeadComponent found");
            return;
        }
        super.processEvent(event);
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(FacesContext context) throws IOException {
        // empty
    }
}
