/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.output.visnetwork;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;
import org.primefaces.component.api.Widget;

@FacesComponent(VisNetworkComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "primefaces", name = "components.js"),
    @ResourceDependency(library = "vis-network", name = "vis-data.min.js"),
    @ResourceDependency(library = "vis-network", name = "vis-network.min.js"),
    @ResourceDependency(library = "vis-network", name = "vis-network.min.css"),
    @ResourceDependency(library = "ejsf", name = "vis-network.js")
})
public class VisNetworkComponent extends UIOutput implements Widget {

    public static final String COMPONENT_TYPE = "ejsf.visNetworkComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

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

    public Integer getWidth() {
        return (Integer) getStateHelper().eval(PropertyKeys.width, null);
    }

    public void setWidth(Integer width) {
        getStateHelper().put(PropertyKeys.width, width);
    }

    public Integer getHeight() {
        return (Integer) getStateHelper().eval(PropertyKeys.height, null);
    }

    public void setHeight(Integer height) {
        getStateHelper().put(PropertyKeys.height, height);
    }
}
