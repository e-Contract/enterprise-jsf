package be.e_contract.jsf.taglib.table;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialResponseWriter;
import javax.faces.context.ResponseWriter;

public class PartialUpdatePartialResponseWriter extends PartialResponseWriter {

    private static final String PARTIAL_UPDATE_LISTENERS_ATTRIBUTE = PartialUpdatePartialResponseWriter.class.getName() + ".partialUpdateListeners";

    public PartialUpdatePartialResponseWriter(ResponseWriter writer) {
        super(writer);
    }

    @Override
    public void endDocument() throws IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        List<PartialUpdateListener> partialUpdateListeners = getPartialUpdateListeners(facesContext);
        for (PartialUpdateListener partialUpdateListener : partialUpdateListeners) {
            partialUpdateListener.partialUpdate(facesContext, this);
        }
        super.endDocument();
    }

    private static List<PartialUpdateListener> getPartialUpdateListeners(FacesContext facesContext) {
        Map<Object, Object> attributes = facesContext.getAttributes();
        List<PartialUpdateListener> partialUpdateListeners = (List<PartialUpdateListener>) attributes.get(PARTIAL_UPDATE_LISTENERS_ATTRIBUTE);
        if (null == partialUpdateListeners) {
            partialUpdateListeners = new LinkedList<>();
            attributes.put(PARTIAL_UPDATE_LISTENERS_ATTRIBUTE, partialUpdateListeners);
        }
        return partialUpdateListeners;
    }

    public static void registerPartialUpdateListener(FacesContext facesContext, PartialUpdateListener listener) {
        List<PartialUpdateListener> partialUpdateListeners = getPartialUpdateListeners(facesContext);
        partialUpdateListeners.add(listener);
    }
}
