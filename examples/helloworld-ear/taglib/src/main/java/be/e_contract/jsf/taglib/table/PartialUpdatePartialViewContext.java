package be.e_contract.jsf.taglib.table;

import javax.faces.context.PartialResponseWriter;
import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextWrapper;

public class PartialUpdatePartialViewContext extends PartialViewContextWrapper {

    private final PartialResponseWriter writer;

    public PartialUpdatePartialViewContext(PartialViewContext wrapped) {
        super(wrapped);
        PartialResponseWriter originalWriter = wrapped.getPartialResponseWriter();
        this.writer = new PartialUpdatePartialResponseWriter(originalWriter);
    }

    @Override
    public PartialResponseWriter getPartialResponseWriter() {
        return this.writer;
    }
}
