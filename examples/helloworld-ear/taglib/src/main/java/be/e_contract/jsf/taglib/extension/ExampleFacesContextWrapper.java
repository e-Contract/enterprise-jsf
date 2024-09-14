package be.e_contract.jsf.taglib.extension;

import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextWrapper;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;

public class ExampleFacesContextWrapper extends FacesContextWrapper {

    public ExampleFacesContextWrapper(FacesContext wrapped) {
        super(wrapped);
    }

    @Override
    public void setResponseWriter(ResponseWriter responseWriter) {
        if (alreadyWrapped(responseWriter)) {
            super.setResponseWriter(responseWriter);
        } else {
            ExampleResponseWriterWrapper wrapper = new ExampleResponseWriterWrapper(responseWriter);
            super.setResponseWriter(wrapper);
        }
    }

    private boolean alreadyWrapped(ResponseWriter responseWriter) {
        while (responseWriter != null) {
            if (responseWriter instanceof ExampleResponseWriterWrapper) {
                return true;
            }
            if (responseWriter instanceof ResponseWriterWrapper) {
                ResponseWriterWrapper wrapper = (ResponseWriterWrapper) responseWriter;
                responseWriter = wrapper.getWrapped();
            } else {
                return false;
            }
        }
        return false;
    }
}
