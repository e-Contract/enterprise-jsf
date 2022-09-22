package be.e_contract.jsf.taglib;

import java.io.IOException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent(ExampleWebComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "javax.faces", name = "jsf.js"),
    @ResourceDependency(library = "ejsf", name = "ejsf-web-component-example.js")
})
public class ExampleWebComponent extends UIOutput {

    public static final String COMPONENT_TYPE = "ejsf.exampleWebComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public ExampleWebComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        String value = (String) getValue();
        ResponseWriter responseWriter = context.getResponseWriter();
        String clientId = super.getClientId(context);
        responseWriter.startElement("example-web-component", this);
        responseWriter.writeAttribute("id", clientId, "id");
        responseWriter.writeAttribute("data-value", value, "value");
        responseWriter.endElement("example-web-component");
    }
}
