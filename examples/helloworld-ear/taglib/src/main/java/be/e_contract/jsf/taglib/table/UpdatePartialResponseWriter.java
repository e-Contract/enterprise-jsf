package be.e_contract.jsf.taglib.table;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialResponseWriter;
import javax.faces.context.ResponseWriter;

public class UpdatePartialResponseWriter extends PartialResponseWriter {

    private static final String DATA_ATTRIBUTE = UpdatePartialResponseWriter.class.getName() + ".data";

    public UpdatePartialResponseWriter(ResponseWriter writer) {
        super(writer);
    }

    @Override
    public void endDocument() throws IOException {
        Map<String, String> attributes = Collections.singletonMap("id", "widget-updates");
        startExtension(attributes);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map<String, String> updateData = getUpdateData(facesContext);
        for (Map.Entry<String, String> updateEntry : updateData.entrySet()) {
            String clientId = updateEntry.getKey();
            String data = updateEntry.getValue();
            startElement("update", null);
            writeAttribute("widget", clientId, null);
            write(data);
            endElement("update");
        }
        endExtension();
        super.endDocument();
    }

    private static Map<String, String> getUpdateData(FacesContext facesContext) {
        Map<Object, Object> attributes = facesContext.getAttributes();
        Map<String, String> updateData = (Map<String, String>) attributes.get(DATA_ATTRIBUTE);
        if (null == updateData) {
            updateData = new HashMap<>();
            attributes.put(DATA_ATTRIBUTE, updateData);
        }
        return updateData;
    }

    public static void updateWidget(FacesContext facesContext, UIComponent widgetComponent, String data) {
        Map<String, String> updateData = getUpdateData(facesContext);
        String clientId = widgetComponent.getClientId(facesContext);
        updateData.put(clientId, data);
    }
}
