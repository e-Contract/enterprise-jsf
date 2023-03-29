package be.e_contract.jsf.taglib;

import java.io.IOException;
import java.util.List;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent(PanelGridComponent.COMPONENT_TYPE)
public class PanelGridComponent extends UIComponentBase {

    public static final String COMPONENT_TYPE = "ejsf.panelGrid";

    public static final String COMPONENT_FAMILY = "ejsf";

    public PanelGridComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        columns,
    }

    public int getColumns() {
        return (int) getStateHelper().eval(PropertyKeys.columns, 1);
    }

    public void setColumns(int columns) {
        getStateHelper().put(PropertyKeys.columns, columns);
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        ResponseWriter responseWriter = context.getResponseWriter();
        String clientId = super.getClientId(context);
        responseWriter.startElement("table", this);
        responseWriter.writeAttribute("id", clientId, "id");
    }

    @Override
    public void encodeChildren(FacesContext context) throws IOException {
        List<UIComponent> children = getChildren();
        int columns = getColumns();
        ResponseWriter responseWriter = context.getResponseWriter();
        for (int idx = 0; idx < children.size(); idx++) {
            boolean renderRow = (idx % columns) == 0;
            if (renderRow) {
                if (idx != 0) {
                    responseWriter.endElement("tr");
                }
                responseWriter.startElement("tr", null);
            }
            responseWriter.startElement("td", null);
            UIComponent child = children.get(idx);
            child.encodeAll(context);
            responseWriter.endElement("td");
        }
        if (!children.isEmpty()) {
            responseWriter.endElement("tr");
        }
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
        ResponseWriter responseWriter = context.getResponseWriter();
        responseWriter.endElement("table");
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
