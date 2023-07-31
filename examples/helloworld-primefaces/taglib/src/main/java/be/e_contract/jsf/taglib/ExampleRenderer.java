package be.e_contract.jsf.taglib;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

@FacesRenderer(componentFamily = ExampleComponent.COMPONENT_FAMILY,
        rendererType = ExampleRenderer.RENDERER_TYPE)
public class ExampleRenderer extends CoreRenderer {

    public static final String RENDERER_TYPE = "ejsf.exampleRenderer";

    @Override
    public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
        ExampleComponent exampleComponent = (ExampleComponent) component;
        String initialValue = (String) exampleComponent.getValue();
        ResponseWriter responseWriter = facesContext.getResponseWriter();
        String clientId = exampleComponent.getClientId(facesContext);

        responseWriter.startElement("span", exampleComponent);
        responseWriter.writeAttribute("id", clientId, "id");
        if (initialValue != null) {
            responseWriter.writeText(initialValue, "value");
        }
        responseWriter.endElement("span");

        WidgetBuilder widgetBuilder = getWidgetBuilder(facesContext);
        widgetBuilder.init("ExampleWidget", exampleComponent);
        widgetBuilder.attr("initialValue", initialValue);
        widgetBuilder.finish();
    }
}
