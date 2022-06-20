package be.e_contract.jsf.taglib.output;

import java.io.IOException;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent(ExampleOutput.COMPONENT_TYPE)
public class ExampleOutput extends UIOutput {

    public static final String COMPONENT_TYPE = "ejsf.exampleOutput";

    public static final String COMPONENT_FAMILY = "ejsf";

    public ExampleOutput() {
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
        responseWriter.startElement("span", this);
        responseWriter.writeAttribute("id", clientId, "id");
        responseWriter.write("hello: " + value);
        responseWriter.endElement("span");
    }
}
