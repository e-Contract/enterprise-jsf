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

    private final List<UIComponent> extensions;

    public ExamplePartialResponseWriter(ResponseWriter writer) {
        super(writer);
        this.extensions = new LinkedList<>();
    }

    @Override
    public void startElement(String name, UIComponent component) throws IOException {
        super.startElement(name, component);
        if (component instanceof Extension) {
            this.extensions.add(component);
        }
    }

    @Override
    public void endDocument() throws IOException {
        Map<String, String> attributes = Collections.singletonMap("id", "example");
        startExtension(attributes);
        ExtensionUtil.writeExtensionData(this.extensions, this);
        endExtension();
        super.endDocument();
    }

}
