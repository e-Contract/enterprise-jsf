/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.output.visnetwork;

import java.io.IOException;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.primefaces.PrimeFaces;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

@FacesRenderer(componentFamily = VisNetworkComponent.COMPONENT_FAMILY,
        rendererType = VisNetworkRenderer.RENDERER_TYPE)
public class VisNetworkRenderer extends CoreRenderer {

    public static final String RENDERER_TYPE = "ejsf.visNetworkRenderer";

    @Override
    public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
        VisNetworkComponent visNetworkComponent = (VisNetworkComponent) component;
        ResponseWriter responseWriter = facesContext.getResponseWriter();
        String clientId = visNetworkComponent.getClientId(facesContext);

        responseWriter.startElement("div", visNetworkComponent);
        responseWriter.writeAttribute("id", clientId, "id");
        Integer width = visNetworkComponent.getWidth();
        if (null == width) {
            width = 600;
        }
        Integer height = visNetworkComponent.getHeight();
        if (null == height) {
            height = 400;
        }
        String style = "width: " + width + "px; height: " + height + "px;";
        responseWriter.writeAttribute("style", style, null);
        responseWriter.endElement("div");

        WidgetBuilder widgetBuilder = getWidgetBuilder(facesContext);
        widgetBuilder.init("EJSFVisNetwork", visNetworkComponent);
        encodeClientBehaviors(facesContext, visNetworkComponent);
        widgetBuilder.finish();
    }

    @Override
    public void decode(FacesContext context, UIComponent component) {
        ExternalContext externalContext = context.getExternalContext();
        Map<String, String> requestParameterMap = externalContext.getRequestParameterMap();
        String clientId = component.getClientId(context);
        if (requestParameterMap.containsKey(clientId + "_request_data")) {
            VisNetworkComponent visNetworkComponent = (VisNetworkComponent) component;
            String value = (String) visNetworkComponent.getValue();
            PrimeFaces.current().ajax().addCallbackParam("data", value);
            context.renderResponse();
            return;
        }
        decodeBehaviors(context, component);
    }
}
