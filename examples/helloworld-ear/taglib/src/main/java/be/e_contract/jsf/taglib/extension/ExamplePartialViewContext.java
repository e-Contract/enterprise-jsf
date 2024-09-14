package be.e_contract.jsf.taglib.extension;

import javax.faces.context.PartialResponseWriter;
import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextWrapper;

public class ExamplePartialViewContext extends PartialViewContextWrapper {

    private final PartialResponseWriter writer;

    public ExamplePartialViewContext(PartialViewContext wrapped) {
        super(wrapped);
        PartialResponseWriter originalWriter = wrapped.getPartialResponseWriter();
        this.writer = new ExamplePartialResponseWriter(originalWriter);
    }

    @Override
    public PartialResponseWriter getPartialResponseWriter() {
        return this.writer;
    }
}
