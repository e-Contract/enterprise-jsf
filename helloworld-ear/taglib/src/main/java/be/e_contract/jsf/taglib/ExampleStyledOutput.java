package be.e_contract.jsf.taglib;

import java.io.IOException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent(ExampleStyledOutput.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "ejsf", name = "example-styled-output.css")
})
public class ExampleStyledOutput extends UIOutput {

    public static final String COMPONENT_TYPE = "ejsf.exampleStyledOutput";

    public static final String COMPONENT_FAMILY = "ejsf";

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        ExampleEnum value = (ExampleEnum) getValue();
        ResponseWriter responseWriter = context.getResponseWriter();
        String clientId = super.getClientId(context);
        responseWriter.startElement("span", this);
        responseWriter.writeAttribute("id", clientId, "id");
        responseWriter.writeAttribute("class", "example-styled-output "
                + "example-styled-output-" + value.name(), null);
        responseWriter.write(value.toString());
        responseWriter.endElement("span");
    }
}
