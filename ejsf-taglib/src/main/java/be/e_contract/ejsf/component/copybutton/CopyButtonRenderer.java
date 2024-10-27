/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.copybutton;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

@FacesRenderer(
        componentFamily = CopyButtonComponent.COMPONENT_FAMILY,
        rendererType = CopyButtonRenderer.RENDERER_TYPE
)
public class CopyButtonRenderer extends CoreRenderer {

    public static final String RENDERER_TYPE = "ejsf.copyButtonRenderer";

    @Override
    public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
        CopyButtonComponent copyButtonComponent = (CopyButtonComponent) component;
        ResponseWriter responseWriter = facesContext.getResponseWriter();
        String clientId = copyButtonComponent.getClientId(facesContext);

        responseWriter.startElement("span", copyButtonComponent);
        responseWriter.writeAttribute("id", clientId, "id");
        responseWriter.writeAttribute("style", "cursor: pointer;", null);

        responseWriter.startElement("i", null);
        responseWriter.writeAttribute("id", clientId + ":icon", null);
        responseWriter.writeAttribute("class", "pi pi-copy", null);
        String style = copyButtonComponent.getStyle();
        if (null != style) {
            responseWriter.writeAttribute("style", style, "style");
        }
        responseWriter.endElement("i");

        responseWriter.endElement("span");

        String value = copyButtonComponent.getValue();
        WidgetBuilder widgetBuilder = getWidgetBuilder(facesContext);
        widgetBuilder.init("EJSFCopyButton", copyButtonComponent);
        widgetBuilder.attr("value", value);
        widgetBuilder.finish();
    }
}
