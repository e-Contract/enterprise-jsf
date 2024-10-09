package be.e_contract.jsf.taglib.table;

import java.io.IOException;
import javax.faces.context.ResponseWriter;

public class HTMLEncoder {

    private final ResponseWriter responseWriter;

    public HTMLEncoder(ResponseWriter responseWriter) {
        this.responseWriter = responseWriter;
    }

    public void element(String name, IOExceptionConsumer content) throws IOException {
        this.responseWriter.startElement(name, null);
        content.accept();
        this.responseWriter.endElement(name);
    }

    public void attr(String name, Object value) throws IOException {
        this.responseWriter.writeAttribute(name, value, null);
    }

    public void content(String content) throws IOException {
        this.responseWriter.writeText(content, null);
    }

    @FunctionalInterface
    public interface IOExceptionConsumer {

        void accept() throws IOException;
    }
}
