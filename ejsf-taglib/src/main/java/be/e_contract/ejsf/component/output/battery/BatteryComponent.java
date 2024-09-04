/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.output.battery;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;

@FacesComponent(BatteryComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "ejsf", name = "battery.js")
})
public class BatteryComponent extends UIOutput {

    public static final String COMPONENT_TYPE = "ejsf.batteryComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public BatteryComponent() {
        setRendererType(BatteryRenderer.RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        height,
        width,
    }

    public void setHeight(int height) {
        getStateHelper().put(PropertyKeys.height, height);
    }

    public int getHeight() {
        return (int) getStateHelper().get(PropertyKeys.height);
    }

    public void setWidth(int width) {
        getStateHelper().put(PropertyKeys.width, width);
    }

    public int getWidth() {
        return (int) getStateHelper().get(PropertyKeys.width);
    }
}
