package be.e_contract.jsf.taglib;

import java.io.IOException;
import java.util.Map;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent(ExampleInput.COMPONENT_TYPE)
public class ExampleInput extends UIInput {

    public static final String COMPONENT_TYPE = "ejsf.exampleInput";

    public static final String COMPONENT_FAMILY = "ejsf";

    public ExampleInput() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        ResponseWriter responseWriter = context.getResponseWriter();
        String clientId = super.getClientId(context);
        char separatorChar = UINamingContainer.getSeparatorChar(context);

        responseWriter.startElement("span", this);
        responseWriter.writeAttribute("id", clientId, "id");

        responseWriter.startElement("input", this);
        responseWriter.writeAttribute("name", clientId + separatorChar + "input", null);
        responseWriter.writeAttribute("type", "text", null);
        Object value = getSubmittedValue();
        if (null == value) {
            value = getValue();
        }
        if (value != null) {
            responseWriter.writeAttribute("value", value.toString(), "value");
        }
        responseWriter.endElement("input");

        responseWriter.endElement("span");
    }

    @Override
    public void decode(FacesContext context) {
        Map<String, String> requestParameterMap
                = context.getExternalContext().getRequestParameterMap();
        String clientId = getClientId(context);
        char separatorChar = UINamingContainer.getSeparatorChar(context);
        String submittedValue
                = (String) requestParameterMap.get(clientId + separatorChar + "input");
        if (null != submittedValue) {
            setSubmittedValue(submittedValue);
            setValid(true);
        }
    }
}
