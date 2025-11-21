/*
 * Enterprise JSF project.
 *
 * Copyright 2024-2025 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.output.llm;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

@FacesRenderer(
        componentFamily = OutputLargeLanguageModelComponent.COMPONENT_FAMILY,
        rendererType = OutputLargeLanguageModelRenderer.RENDERER_TYPE
)
public class OutputLargeLanguageModelRenderer extends CoreRenderer {

    public static final String RENDERER_TYPE = "ejsf.outputLLMRenderer";

    @Override
    public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
        OutputLargeLanguageModelComponent outputLargeLanguageModelComponent = (OutputLargeLanguageModelComponent) component;

        ResponseWriter responseWriter = facesContext.getResponseWriter();
        responseWriter.startElement("span", outputLargeLanguageModelComponent);
        String clientId = outputLargeLanguageModelComponent.getClientId(facesContext);
        responseWriter.writeAttribute("id", clientId, "id");
        responseWriter.endElement("span");

        String value = (String) outputLargeLanguageModelComponent.getValue();
        WidgetBuilder widgetBuilder = getWidgetBuilder(facesContext);
        widgetBuilder.init("EJSFOutputLLM", outputLargeLanguageModelComponent);
        widgetBuilder.attr("value", value);
        widgetBuilder.finish();
    }
}
