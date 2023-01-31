/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.geolocation;

import javax.el.MethodExpression;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;

@FacesComponent(GeolocationListenerComponent.COMPONENT_TYPE)
public class GeolocationListenerComponent extends UIComponentBase {

    public static final String COMPONENT_TYPE = "ejsf.geolocationListener";

    public static final String COMPONENT_FAMILY = "ejsf";

    public enum PropertyKeys {
        action
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public void setAction(MethodExpression methodExpression) {
        getStateHelper().put(PropertyKeys.action, methodExpression);
    }

    public MethodExpression getAction() {
        return (MethodExpression) getStateHelper().eval(PropertyKeys.action);
    }
}
