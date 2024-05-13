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
        zoomControl,
        zoom,
        minZoom,
        maxZoom,
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

    public void setZoomControl(boolean zoomControl) {
        getStateHelper().put(PropertyKeys.zoomControl, zoomControl);
    }

    public boolean isZoomControl() {
        return (Boolean) getStateHelper().eval(PropertyKeys.zoomControl, true);
    }

    public void setZoom(int zoom) {
        getStateHelper().put(PropertyKeys.zoom, zoom);
    }

    public int getZoom() {
        return (int) getStateHelper().eval(PropertyKeys.zoom, 13);
    }

    public void setMinZoom(int minZoom) {
        getStateHelper().put(PropertyKeys.minZoom, minZoom);
    }

    public int getMinZoom() {
        return (int) getStateHelper().eval(PropertyKeys.minZoom, 1);
    }

    public void setMaxZoom(int maxZoom) {
        getStateHelper().put(PropertyKeys.maxZoom, maxZoom);
    }

    public int getMaxZoom() {
        return (int) getStateHelper().eval(PropertyKeys.maxZoom, 19);
    }
}
