/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;

@FacesComponent(MonkeyPatchComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "primefaces", name = "components.js"),
    @ResourceDependency(library = "ejsf", name = "monkey-patch.js")
})
public class MonkeyPatchComponent extends UIComponentBase {

    public static final String COMPONENT_TYPE = "ejsf.monkeyPatchComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public MonkeyPatchComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }
}
