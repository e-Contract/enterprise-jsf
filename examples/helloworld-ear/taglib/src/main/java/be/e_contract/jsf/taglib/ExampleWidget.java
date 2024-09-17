package be.e_contract.jsf.taglib;

import java.io.IOException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent(ExampleWidget.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "javax.faces", name = "jsf.js"),
    @ResourceDependency(library = "ejsf", name = "ejsf-widget.js"),
    @ResourceDependency(library = "ejsf", name = "ejsf-widget-example.js")
})
public class ExampleWidget extends UIOutput {

    public static final String COMPONENT_TYPE = "ejsf.exampleWidget";

    public static final String COMPONENT_FAMILY = "ejsf";

    public ExampleWidget() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        widgetVar
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        if (!isRendered()) {
            return;
        }
        String value = (String) getValue();
        ResponseWriter responseWriter = context.getResponseWriter();
        String clientId = super.getClientId(context);
        responseWriter.startElement("span", this);
        responseWriter.writeAttribute("id", clientId, "id");
        responseWriter.writeAttribute("data-ejsf-widget-type", "ExampleWidget", null);
        responseWriter.writeAttribute("data-ejsf-widget-update", 1, null);
        String widgetVar = getWidgetVar();
        if (null != widgetVar) {
            responseWriter.writeAttribute("data-ejsf-widget-var", widgetVar, null);
        }
        responseWriter.writeText(value, "value");
        responseWriter.endElement("span");
    }
}
