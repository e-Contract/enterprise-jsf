package be.e_contract.jsf.taglib;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;
import org.primefaces.component.api.Widget;

@FacesComponent(EChartsComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "primefaces", name = "components.js"),
    @ResourceDependency(library = "echarts", name = "echarts.min.js"),
    @ResourceDependency(library = "ejsf", name = "echarts-widget.js")
})
public class EChartsComponent extends UIOutput implements Widget {

    public static final String COMPONENT_TYPE = "ejsf.echartsComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public EChartsComponent() {
        setRendererType(EChartsRenderer.RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        widgetVar,
        width,
        height
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public int getWidth() {
        return (int) getStateHelper().eval(PropertyKeys.width, 600);
    }

    public void setWidth(int width) {
        getStateHelper().put(PropertyKeys.width, width);
    }

    public int getHeight() {
        return (int) getStateHelper().eval(PropertyKeys.height, 800);
    }

    public void setHeight(int height) {
        getStateHelper().put(PropertyKeys.height, height);
    }
}
