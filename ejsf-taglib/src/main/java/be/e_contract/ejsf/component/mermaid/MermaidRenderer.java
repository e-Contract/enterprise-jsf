/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.mermaid;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

@FacesRenderer(componentFamily = MermaidComponent.COMPONENT_FAMILY,
        rendererType = MermaidRenderer.RENDERER_TYPE)
public class MermaidRenderer extends CoreRenderer {

    public static final String RENDERER_TYPE = "ejsf.mermaidRenderer";

    @Override
    public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
        MermaidComponent mermaidComponent = (MermaidComponent) component;
        ResponseWriter responseWriter = facesContext.getResponseWriter();
        String clientId = mermaidComponent.getClientId(facesContext);

        responseWriter.startElement("pre", mermaidComponent);
        responseWriter.writeAttribute("id", clientId, "id");
        responseWriter.writeAttribute("class", "mermaid", null);

        String diagram = (String) mermaidComponent.getValue();
        responseWriter.write(diagram);

        responseWriter.endElement("pre");

        WidgetBuilder widgetBuilder = getWidgetBuilder(facesContext);
        widgetBuilder.init("EJSFMermaid", mermaidComponent);
        widgetBuilder.finish();
    }
}
