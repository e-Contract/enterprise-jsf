package be.e_contract.jsf.taglib.extension;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.PartialResponseWriter;
import javax.faces.context.ResponseWriter;

public class ExamplePartialResponseWriter extends PartialResponseWriter {

    private final List<String> clientIds;

    public ExamplePartialResponseWriter(ResponseWriter writer) {
        super(writer);
        this.clientIds = new LinkedList<>();
    }

    @Override
    public void startElement(String name, UIComponent component) throws IOException {
        super.startElement(name, component);
        if (component instanceof Extension) {
            String clientId = component.getId();
            this.clientIds.add(clientId);
        }
    }

    @Override
    public void endDocument() throws IOException {
        Map<String, String> attributes = Collections.singletonMap("id", "example");
        startExtension(attributes);
        write("[");
        boolean first = true;
        for (String clientId : this.clientIds) {
            if (!first) {
                write(",");
            }
            first = false;
            write("\"" + clientId + "\"");
        }
        write("]");
        endExtension();
        super.endDocument();
    }

}
