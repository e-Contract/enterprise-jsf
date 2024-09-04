/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.output.battery;

import java.io.IOException;
import java.util.Collection;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesRenderer(componentFamily = BatteryComponent.COMPONENT_FAMILY, rendererType = BatteryRenderer.RENDERER_TYPE)
public class BatteryRenderer extends Renderer {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatteryRenderer.class);

    public static final String RENDERER_TYPE = "ejsf.batteryRenderer";

    @Override
    public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
        BatteryComponent batteryComponent = (BatteryComponent) component;
        String clientId = batteryComponent.getClientId();
        ResponseWriter responseWriter = facesContext.getResponseWriter();
        responseWriter.startElement("span", batteryComponent);
        responseWriter.writeAttribute("id", clientId, "id");
        responseWriter.writeAttribute("data-ejsf-battery", "", null);
        Object value = batteryComponent.getValue();
        if (value instanceof Number) {
            Number number = (Number) value;
            int intValue = number.intValue();
            responseWriter.writeAttribute("data-ejsf-battery-value", intValue, null);
        }
        responseWriter.writeAttribute("data-ejsf-update", 1, null);
        responseWriter.endElement("span");

        if (renderCanvas(facesContext, batteryComponent)) {
            responseWriter.startElement("canvas", batteryComponent);
            responseWriter.writeAttribute("data-ejsf-battery-render", clientId, null);
            int height = batteryComponent.getHeight();
            responseWriter.writeAttribute("height", height, "height");
            int width = batteryComponent.getWidth();
            responseWriter.writeAttribute("width", width, "width");
            responseWriter.writeAttribute("data-ejsf-update", 1, null);
            responseWriter.endElement("canvas");
        }
    }

    private boolean renderCanvas(FacesContext facesContext, BatteryComponent batteryComponent) {
        if (!facesContext.isPostback()) {
            return true;
        }
        PartialViewContext partialViewContext = facesContext.getPartialViewContext();
        Collection<String> renderIds = partialViewContext.getRenderIds();
        // if we're not in the set, it was one of our parents
        return !renderIds.contains(batteryComponent.getClientId(facesContext));
    }
}
