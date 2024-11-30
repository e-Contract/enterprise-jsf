/*
 * Enterprise JSF project.
 *
 * Copyright 2023-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.mermaid;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.RenderKit;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.renderkit.RendererUtils;
import org.primefaces.util.FastStringWriter;
import org.primefaces.util.WidgetBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesRenderer(componentFamily = MermaidComponent.COMPONENT_FAMILY,
        rendererType = MermaidRenderer.RENDERER_TYPE)
public class MermaidRenderer extends CoreRenderer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MermaidRenderer.class);

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
        if (null == diagram) {
            FastStringWriter output = new FastStringWriter();
            ResponseWriter originalResponseWriter = facesContext.getResponseWriter();
            if (null != originalResponseWriter) {
                facesContext.setResponseWriter(originalResponseWriter.cloneWithWriter(output));
            } else {
                RenderKit renderKit = RendererUtils.getRenderKit(facesContext);
                facesContext.setResponseWriter(renderKit.createResponseWriter(output, "text/html", "UTF-8"));
            }
            try {
                for (UIComponent child : mermaidComponent.getChildren()) {
                    child.encodeAll(facesContext);
                }
            } catch (IOException ex) {
                LOGGER.error("I/O error: " + ex.getMessage(), ex);
            } finally {
                if (null != originalResponseWriter) {
                    facesContext.setResponseWriter(originalResponseWriter);
                }
            }
            diagram = output.toString();
        }
        responseWriter.write(diagram);

        responseWriter.endElement("pre");

        WidgetBuilder widgetBuilder = getWidgetBuilder(facesContext);
        widgetBuilder.init("EJSFMermaid", mermaidComponent);
        widgetBuilder.finish();
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        // empty
    }
}
