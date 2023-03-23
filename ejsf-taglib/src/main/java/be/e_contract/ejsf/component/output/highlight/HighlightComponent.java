/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.output.highlight;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;
import org.primefaces.component.api.Widget;

@FacesComponent(HighlightComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "primefaces", name = "components.js"),
    @ResourceDependency(library = "highlight", name = "highlight.min.js"),
    @ResourceDependency(library = "highlight", name = "default.css"),
    @ResourceDependency(library = "ejsf", name = "highlight.js")
})
public class HighlightComponent extends UIOutput implements Widget {

    public static final String COMPONENT_TYPE = "ejsf.highlightComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public HighlightComponent() {
        setRendererType(HighlightRenderer.RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        language,
        _for,
        resource
    }

    public String getLanguage() {
        return (String) getStateHelper().eval(PropertyKeys.language);
    }

    public void setLanguage(String language) {
        getStateHelper().put(PropertyKeys.language, language);
    }

    public String getFor() {
        return (String) getStateHelper().eval(PropertyKeys._for);
    }

    public void setFor(String _for) {
        getStateHelper().put(PropertyKeys._for, _for);
    }

    public String getResource() {
        return (String) getStateHelper().eval(PropertyKeys.resource);
    }

    public void setResource(String resource) {
        getStateHelper().put(PropertyKeys.resource, resource);
    }
}
