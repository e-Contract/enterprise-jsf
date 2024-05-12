/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.leaflet;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;
import org.primefaces.component.api.Widget;

@FacesComponent(LeafletComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "primefaces", name = "components.js"),
    @ResourceDependency(library = "ejsf", name = "leaflet/leaflet.css"),
    @ResourceDependency(library = "ejsf", name = "leaflet/leaflet.js"),
    @ResourceDependency(library = "ejsf", name = "leaflet.js")
})
public class LeafletComponent extends UIOutput implements Widget {

    public static final String COMPONENT_TYPE = "ejsf.leafletComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public LeafletComponent() {
        setRendererType(LeafletRenderer.RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        widgetVar,
        height,
        width,
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public void setHeight(String height) {
        getStateHelper().put(PropertyKeys.height, height);
    }

    public String getHeight() {
        return (String) getStateHelper().eval(PropertyKeys.height, "200px");
    }

    public void setWidth(String width) {
        getStateHelper().put(PropertyKeys.width, width);
    }

    public String getWidth() {
        return (String) getStateHelper().eval(PropertyKeys.width);
    }
}
