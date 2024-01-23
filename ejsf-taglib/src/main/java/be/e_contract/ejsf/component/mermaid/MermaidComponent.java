/*
 * Enterprise JSF project.
 *
 * Copyright 2023-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.mermaid;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;
import org.primefaces.component.api.Widget;

@FacesComponent(MermaidComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "primefaces", name = "components.js"),
    @ResourceDependency(library = "ejsf", name = "mermaid/mermaid.min.js"),
    @ResourceDependency(library = "ejsf", name = "mermaid.js")
})
public class MermaidComponent extends UIOutput implements Widget {

    public static final String COMPONENT_TYPE = "ejsf.mermaidComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public MermaidComponent() {
        setRendererType(MermaidRenderer.RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        widgetVar,
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }
}
