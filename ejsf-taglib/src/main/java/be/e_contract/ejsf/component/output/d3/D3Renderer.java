/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.output.d3;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

@FacesRenderer(componentFamily = D3Component.COMPONENT_FAMILY,
        rendererType = D3Renderer.RENDERER_TYPE)
public class D3Renderer extends CoreRenderer {

    public static final String RENDERER_TYPE = "ejsf.d3Renderer";

    @Override
    public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
        D3Component d3Component = (D3Component) component;
        ResponseWriter responseWriter = facesContext.getResponseWriter();
        String clientId = d3Component.getClientId(facesContext);

        responseWriter.startElement("div", d3Component);
        responseWriter.writeAttribute("id", clientId, "id");
        Integer width = d3Component.getWidth();
        if (null == width) {
            width = 600;
        }
        Integer height = d3Component.getHeight();
        if (null == height) {
            height = 400;
        }
        String style = "width: " + width + "px; height: " + height + "px;";
        responseWriter.writeAttribute("style", style, null);
        responseWriter.endElement("div");

        WidgetBuilder widgetBuilder = getWidgetBuilder(facesContext);
        widgetBuilder.init("EJSFD3", d3Component);
        widgetBuilder.attr("WIDTH", width);
        widgetBuilder.attr("HEIGHT", height);
        widgetBuilder.finish();
    }
}
