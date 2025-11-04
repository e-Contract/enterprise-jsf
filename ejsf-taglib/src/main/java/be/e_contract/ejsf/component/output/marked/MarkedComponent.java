/*
 * Enterprise JSF project.
 *
 * Copyright 2024-2025 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.output.marked;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;
import org.primefaces.component.api.Widget;

@FacesComponent(MarkedComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "primefaces", name = "components.js"),
    @ResourceDependency(library = "ejsf", name = "dompurify/purify.min.js"),
    @ResourceDependency(library = "ejsf", name = "marked/marked.umd.js"),
    @ResourceDependency(library = "ejsf", name = "marked.js")
})
public class MarkedComponent extends UIOutput implements Widget {

    public static final String COMPONENT_TYPE = "ejsf.markedComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public MarkedComponent() {
        setRendererType(MarkedRenderer.RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public enum PropertyKeys {
        widgetVar,
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }
}
