/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.leaflet;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

@FacesRenderer(componentFamily = LeafletComponent.COMPONENT_FAMILY,
        rendererType = LeafletRenderer.RENDERER_TYPE)
public class LeafletRenderer extends CoreRenderer {

    public static final String RENDERER_TYPE = "ejsf.leafletRenderer";

    @Override
    public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
        LeafletComponent leafletComponent = (LeafletComponent) component;
        ResponseWriter responseWriter = facesContext.getResponseWriter();
        String clientId = leafletComponent.getClientId(facesContext);

        responseWriter.startElement("div", leafletComponent);
        responseWriter.writeAttribute("id", clientId, "id");
        responseWriter.writeAttribute("class", "ejsf-leaflet", null);
        String height = leafletComponent.getHeight();
        responseWriter.writeAttribute("style", "height: " + height, null);

        responseWriter.endElement("div");

        LatLng position = (LatLng) leafletComponent.getValue();
        double latitude = position.getLatitude();
        double longitude = position.getLongitude();

        WidgetBuilder widgetBuilder = getWidgetBuilder(facesContext);
        widgetBuilder.init("EJSFLeaflet", leafletComponent);
        widgetBuilder.attr("latitude", latitude);
        widgetBuilder.attr("longitude", longitude);
        widgetBuilder.finish();
    }
}
