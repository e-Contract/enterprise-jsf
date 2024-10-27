/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.output.marked;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

@FacesRenderer(
        componentFamily = MarkedComponent.COMPONENT_FAMILY,
        rendererType = MarkedRenderer.RENDERER_TYPE
)
public class MarkedRenderer extends CoreRenderer {

    public static final String RENDERER_TYPE = "ejsf.markedRenderer";

    @Override
    public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
        MarkedComponent markedComponent = (MarkedComponent) component;

        ResponseWriter responseWriter = facesContext.getResponseWriter();
        responseWriter.startElement("span", markedComponent);
        String clientId = markedComponent.getClientId(facesContext);
        responseWriter.writeAttribute("id", clientId, "id");
        responseWriter.endElement("span");

        String value = (String) markedComponent.getValue();
        WidgetBuilder widgetBuilder = getWidgetBuilder(facesContext);
        widgetBuilder.init("EJSFMarked", markedComponent);
        widgetBuilder.attr("value", value);
        widgetBuilder.finish();
    }
}
