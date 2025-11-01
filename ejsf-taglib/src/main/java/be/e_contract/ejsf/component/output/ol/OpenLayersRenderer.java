/*
 * Enterprise JSF project.
 *
 * Copyright 2025 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.output.ol;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import java.io.IOException;

@FacesRenderer(
        componentFamily = OpenLayersComponent.COMPONENT_FAMILY,
        rendererType = OpenLayersRenderer.RENDERER_TYPE
)
public class OpenLayersRenderer extends CoreRenderer {

    public static final String RENDERER_TYPE = "ejsf.openLayersRenderer";

    @Override
    public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
        OpenLayersComponent openLayersComponent = (OpenLayersComponent) component;

        ResponseWriter responseWriter = facesContext.getResponseWriter();
        responseWriter.startElement("div", openLayersComponent);
        String clientId = openLayersComponent.getClientId(facesContext);
        responseWriter.writeAttribute("id", clientId, "id");
        String height = openLayersComponent.getHeight();
        String style = "height: " + height;
        String width = openLayersComponent.getWidth();
        if (null != width) {
            style += "; width:" + width;
        }
        responseWriter.writeAttribute("style", style, null);
        responseWriter.endElement("div");

        LatLng position = (LatLng) openLayersComponent.getValue();
        double latitude = position.getLatitude();
        double longitude = position.getLongitude();
        WidgetBuilder widgetBuilder = getWidgetBuilder(facesContext);
        widgetBuilder.init("EJSFOpenLayers", openLayersComponent);
        widgetBuilder.attr("latitude", latitude);
        widgetBuilder.attr("longitude", longitude);
        Integer zoom = openLayersComponent.getZoom();
        widgetBuilder.attr("zoom", zoom);
        widgetBuilder.finish();
    }
}
