package be.e_contract.jsf.taglib.extension;

import java.io.IOException;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

public class ExtensionUtil {

    public static void writeExtensionData(List<UIComponent> extensionComponents, ResponseWriter responseWriter) throws IOException {
        responseWriter.write("{");
        boolean first = true;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        for (UIComponent extensionComponent : extensionComponents) {
            if (!first) {
                responseWriter.write(",");
            }
            first = false;
            String clientId = extensionComponent.getClientId(facesContext);
            responseWriter.write("\"" + clientId + "\":{");
            Extension extension = (Extension) extensionComponent;
            String clientSideData = extension.getClientSideData();
            responseWriter.write(clientSideData);
            responseWriter.write("}");
        }
        responseWriter.write("}");
    }
}
