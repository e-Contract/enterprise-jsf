package be.e_contract.jsf.taglib.table;

import javax.faces.context.PartialResponseWriter;
import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextWrapper;

public class UpdatePartialViewContext extends PartialViewContextWrapper {

    private final PartialResponseWriter writer;

    public UpdatePartialViewContext(PartialViewContext wrapped) {
        super(wrapped);
        PartialResponseWriter originalWriter = wrapped.getPartialResponseWriter();
        this.writer = new UpdatePartialResponseWriter(originalWriter);
    }

    @Override
    public PartialResponseWriter getPartialResponseWriter() {
        return this.writer;
    }
}
