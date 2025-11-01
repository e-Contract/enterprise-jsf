/*
 * Enterprise JSF project.
 *
 * Copyright 2025 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.output.ol;

import org.primefaces.component.api.Widget;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;

@FacesComponent(OpenLayersComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "primefaces", name = "components.js"),
    @ResourceDependency(library = "ejsf", name = "ol/ol.js"),
    @ResourceDependency(library = "ejsf", name = "ol.js")
})
public class OpenLayersComponent extends UIOutput implements Widget {

    public static final String COMPONENT_TYPE = "ejsf.openLayersComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    enum PropertyKeys {
        height,
        width,
        zoom,
    }

    public OpenLayersComponent() {
        setRendererType(OpenLayersRenderer.RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
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

    public void setZoom(int zoom) {
        getStateHelper().put(PropertyKeys.zoom, zoom);
    }

    public int getZoom() {
        return (int) getStateHelper().eval(PropertyKeys.zoom, 15);
    }
}
