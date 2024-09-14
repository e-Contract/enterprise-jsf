package be.e_contract.jsf.taglib.extension;

import java.io.IOException;
import java.time.LocalDateTime;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent(ExtensionComponent.COMPONENT_TYPE)
public class ExtensionComponent extends UIComponentBase implements Extension {

    public static final String COMPONENT_TYPE = "ejsf.extensionComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public ExtensionComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        ResponseWriter responseWriter = context.getResponseWriter();
        responseWriter.startElement("span", this);
        String clientId = getClientId(context);
        responseWriter.writeAttribute("id", clientId, null);
        responseWriter.endElement("span");
    }

    @Override
    public String getClientSideData() {
        return "\"now\": \"" + LocalDateTime.now() + "\"";
    }
}
