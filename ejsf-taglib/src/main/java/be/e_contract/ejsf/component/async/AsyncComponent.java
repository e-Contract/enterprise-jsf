/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.async;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(AsyncComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "ejsf", name = "async.js")
})
public class AsyncComponent extends UIComponentBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.asyncComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public AsyncComponent() {
        setRendererType(AsyncRenderer.RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        value,
        evaluatedValue,
        _for
    }

    public void setValue(String value) {
        getStateHelper().put(PropertyKeys.value, value);
    }

    public String getValue() {
        return (String) getStateHelper().get(PropertyKeys.value);
    }

    public void setEvaluatedValue(Object value) {
        getStateHelper().put(PropertyKeys.evaluatedValue, value);
    }

    public Object getEvaluatedValue() {
        return getStateHelper().get(PropertyKeys.evaluatedValue);
    }

    public void setFor(String _for) {
        getStateHelper().put(PropertyKeys._for, _for);
    }

    public String getFor() {
        return (String) getStateHelper().get(PropertyKeys._for);
    }
}
