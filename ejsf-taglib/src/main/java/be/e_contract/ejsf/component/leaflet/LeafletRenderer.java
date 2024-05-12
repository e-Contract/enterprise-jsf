/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.leaflet;

import java.io.IOException;
import javax.faces.application.Application;
import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesRenderer(componentFamily = LeafletComponent.COMPONENT_FAMILY,
        rendererType = LeafletRenderer.RENDERER_TYPE)
public class LeafletRenderer extends CoreRenderer {

    private static final Logger LOGGER = LoggerFactory.getLogger(LeafletRenderer.class);

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
        String style = "height: " + height;
        String width = leafletComponent.getWidth();
        if (null != width) {
            style += "; width:" + width;
        }
        responseWriter.writeAttribute("style", style, null);

        responseWriter.endElement("div");

        LatLng position = (LatLng) leafletComponent.getValue();
        double latitude = position.getLatitude();
        double longitude = position.getLongitude();

        Application application = facesContext.getApplication();
        ResourceHandler resourceHandler = application.getResourceHandler();
        Resource iconResource = resourceHandler.createResource("leaflet/images/marker-icon.png", "ejsf", "image/png");
        String iconRequestPath = iconResource.getRequestPath();
        LOGGER.debug("icon request path: {}", iconRequestPath);

        Resource shadowResource = resourceHandler.createResource("leaflet/images/marker-shadow.png", "ejsf", "image/png");
        String shadowRequestPath = shadowResource.getRequestPath();

        boolean zoomControl = leafletComponent.isZoomControl();

        WidgetBuilder widgetBuilder = getWidgetBuilder(facesContext);
        widgetBuilder.init("EJSFLeaflet", leafletComponent);
        widgetBuilder.attr("latitude", latitude);
        widgetBuilder.attr("longitude", longitude);
        widgetBuilder.attr("icon_request_path", iconRequestPath);
        widgetBuilder.attr("shadow_request_path", shadowRequestPath);
        widgetBuilder.attr("zoomControl", zoomControl);
        widgetBuilder.finish();
    }
}
