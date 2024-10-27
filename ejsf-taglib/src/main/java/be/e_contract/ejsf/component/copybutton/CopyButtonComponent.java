/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.copybutton;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import org.primefaces.component.api.Widget;

@FacesComponent(CopyButtonComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "ejsf", name = "copy-button.js")
})
public class CopyButtonComponent extends UIComponentBase implements Widget {

    public static final String COMPONENT_TYPE = "ejsf.copyButtonComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public CopyButtonComponent() {
        setRendererType(CopyButtonRenderer.RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        value,
        style
    }

    public String getValue() {
        return (String) getStateHelper().eval(PropertyKeys.value);
    }

    public void setValue(String value) {
        getStateHelper().put(PropertyKeys.value, value);
    }

    public String getStyle() {
        return (String) getStateHelper().get(PropertyKeys.style);
    }

    public void setStyle(String style) {
        getStateHelper().put(PropertyKeys.style, style);
    }
}
