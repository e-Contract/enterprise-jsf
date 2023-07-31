package be.e_contract.jsf.taglib.datalist;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIData;

@FacesComponent(DataList.COMPONENT_TYPE)
public class DataList extends UIData {

    public static final String COMPONENT_TYPE = "ejsf.dataList";

    public static final String COMPONENT_FAMILY = "ejsf";

    public DataList() {
        setRendererType(DataListRenderer.RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }
}
