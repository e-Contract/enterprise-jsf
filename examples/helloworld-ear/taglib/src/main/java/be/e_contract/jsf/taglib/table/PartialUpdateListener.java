package be.e_contract.jsf.taglib.table;

import java.io.IOException;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialResponseWriter;

public interface PartialUpdateListener {

    void partialUpdate(FacesContext facesContext, PartialResponseWriter partialResponseWriter) throws IOException;
}
