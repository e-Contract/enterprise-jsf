package be.e_contract.jsf.taglib.extension;

import java.io.IOException;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(ExtensionComponent.COMPONENT_TYPE)
public class ExtensionComponent extends UIComponentBase implements Extension {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionComponent.class);

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
}
