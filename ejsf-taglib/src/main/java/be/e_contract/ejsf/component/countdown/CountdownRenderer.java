/*
 * Enterprise JSF project.
 *
 * Copyright 2020-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.countdown;

import java.io.IOException;
import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

@FacesRenderer(componentFamily = CountdownComponent.COMPONENT_FAMILY, rendererType = CountdownRenderer.RENDERER_TYPE)
public class CountdownRenderer extends CoreRenderer {

    public static final String RENDERER_TYPE = "ejsf.countdownRenderer";

    @Override
    public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        String clientId = component.getClientId();
        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", "ejsf-countdown", null);
    }

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        CountdownComponent countdownComponent = (CountdownComponent) component;
        ResponseWriter writer = facesContext.getResponseWriter();

        {
            writer.startElement("div", component);
            writer.writeAttribute("class", "indicator", null);
            {
                writer.startElement("div", component);
                writer.writeAttribute("id", component.getClientId() + ":clock", null);
                writer.writeAttribute("style", "display: none;", null);
                {
                    writer.startElement("i", component);
                    writer.writeAttribute("id", component.getClientId() + ":clock-icon", null);
                    writer.writeAttribute("class", "pi pi-clock", null);
                    writer.endElement("i");
                }
                {
                    writer.startElement("span", component);
                    writer.writeAttribute("id", component.getClientId() + ":clock-text", null);
                    writer.writeAttribute("class", "clock-text", null);
                    writer.endElement("span");
                }
                writer.endElement("div");
            }
            {
                writer.startElement("div", component);
                writer.writeAttribute("id", component.getClientId() + ":counter", null);
                writer.writeAttribute("class", "counter", null);
                writer.endElement("div");
            }
            writer.endElement("div");
        }

        writer.endElement("div");

        WidgetBuilder widgetBuilder = getWidgetBuilder(facesContext);
        widgetBuilder.init("EJSFCountdown", countdownComponent);
        Application application = facesContext.getApplication();
        ResourceBundle resourceBundle = application.getResourceBundle(facesContext, "ejsfMessages");
        widgetBuilder.attr("daysAnd", resourceBundle.getString("daysAnd"));
        widgetBuilder.attr("dayAnd", resourceBundle.getString("dayAnd"));
        boolean useHeartbeatTimer = countdownComponent.isUseHeartbeatTimer();
        widgetBuilder.attr("useHeartbeatTimer", useHeartbeatTimer);
        String clockSyncWidgetVar = countdownComponent.getClockSyncWidgetVar();
        if (null != clockSyncWidgetVar) {
            widgetBuilder.attr("clockSyncWidgetVar", clockSyncWidgetVar);
        }
        widgetBuilder.finish();
    }
}
