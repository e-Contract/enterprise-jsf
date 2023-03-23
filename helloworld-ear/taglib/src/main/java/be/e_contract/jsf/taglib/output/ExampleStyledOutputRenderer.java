package be.e_contract.jsf.taglib.output;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;

@FacesRenderer(componentFamily = ExampleStyledOutput.COMPONENT_FAMILY,
        rendererType = ExampleStyledOutputRenderer.RENDERER_TYPE)
public class ExampleStyledOutputRenderer extends Renderer {

    public static final String RENDERER_TYPE = "ejsf.exampleStyledOutputRenderer";

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ExampleStyledOutput exampleStyledOutput = (ExampleStyledOutput) component;
        ExampleEnum value = (ExampleEnum) exampleStyledOutput.getValue();
        ResponseWriter responseWriter = context.getResponseWriter();
        String clientId = exampleStyledOutput.getClientId(context);
        responseWriter.startElement("span", exampleStyledOutput);
        responseWriter.writeAttribute("id", clientId, "id");
        responseWriter.writeAttribute("class", "example-styled-output "
                + "example-styled-output-" + value.name(), null);
        responseWriter.write(value.toString());
        responseWriter.endElement("span");
    }
}
