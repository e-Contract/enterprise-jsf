package be.e_contract.jsf.taglib.datalist;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;

@FacesRenderer(componentFamily = DataList.COMPONENT_FAMILY,
        rendererType = DataListRenderer.RENDERER_TYPE)
public class DataListRenderer extends Renderer {

    public static final String RENDERER_TYPE = "ejsf.dataListRenderer";

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        DataList dataList = (DataList) component;
        ResponseWriter responseWriter = context.getResponseWriter();
        responseWriter.startElement("ul", component);
        String clientId = dataList.getClientId(context);
        responseWriter.writeAttribute("id", clientId, "id");
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        DataList dataList = (DataList) component;
        ResponseWriter responseWriter = context.getResponseWriter();
        for (int idx = 0; idx < dataList.getRowCount(); idx++) {
            dataList.setRowIndex(idx);
            responseWriter.startElement("li", component);
            for (UIComponent child : component.getChildren()) {
                child.encodeAll(context);
            }
            responseWriter.endElement("li");
        }
        dataList.setRowIndex(-1);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter responseWriter = context.getResponseWriter();
        responseWriter.endElement("ul");
    }
}
