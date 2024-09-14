package be.e_contract.jsf.taglib.extension;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;

public class ExampleResponseWriterWrapper extends ResponseWriterWrapper {

    private final List<UIComponent> extensions;

    public ExampleResponseWriterWrapper(ResponseWriter wrapped) {
        super(wrapped);
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
    public void endElement(String name) throws IOException {
        if ("body".equals(name)) {
            super.startElement("template", null);
            writeAttribute("id", "example-extension", null);
            ExtensionUtil.writeExtensionData(this.extensions, this);
            super.endElement("template");
        }
        super.endElement(name);
    }
}
