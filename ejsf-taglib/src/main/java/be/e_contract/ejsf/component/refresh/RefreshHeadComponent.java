/*
 * Enterprise JSF project.
 *
 * Copyright 2025 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.refresh;

import java.io.IOException;
import javax.faces.application.Application;
import javax.faces.application.ViewHandler;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(RefreshHeadComponent.COMPONENT_TYPE)
public class RefreshHeadComponent extends UIComponentBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(RefreshHeadComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.refreshHeadComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public RefreshHeadComponent() {
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

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        if (!isRendered()) {
            return;
        }
        ResponseWriter responseWriter = context.getResponseWriter();
        responseWriter.startElement("meta", this);
        responseWriter.writeAttribute("http-equiv", "refresh", null);
        StringBuilder stringBuffer = new StringBuilder();
        int delay = getDelay();
        stringBuffer.append(delay);
        String viewId = getViewId();
        if (null != viewId) {
            Application application = context.getApplication();
            ViewHandler viewHandler = application.getViewHandler();
            String actionUrl = viewHandler.getActionURL(context, viewId);
            LOGGER.debug("action url: {}", actionUrl);
            stringBuffer.append("; url=");
            stringBuffer.append(actionUrl);
        }
        responseWriter.writeAttribute("content", stringBuffer.toString(), null);
        responseWriter.endElement("meta");
    }
}
