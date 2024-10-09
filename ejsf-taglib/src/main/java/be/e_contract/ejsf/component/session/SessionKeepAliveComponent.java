/*
 * Enterprise JSF project.
 *
 * Copyright 2023-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.session;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import org.primefaces.component.api.Widget;

@FacesComponent(SessionKeepAliveComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "ejsf", name = "session-keep-alive.js")
})
public class SessionKeepAliveComponent extends UIComponentBase implements Widget {

    public static final String COMPONENT_TYPE = "ejsf.sessionKeepAliveComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public SessionKeepAliveComponent() {
        setRendererType(SessionKeepAliveRenderer.RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        pingPeriodBeforeExpiry,
        maxKeepAlivePeriod
    }

    public int getPingPeriodBeforeExpiry() {
        return (int) getStateHelper().eval(PropertyKeys.pingPeriodBeforeExpiry, 30);
    }

    public void setPingPeriodBeforeExpiry(int pingPeriodBeforeExpiry) {
        getStateHelper().put(PropertyKeys.pingPeriodBeforeExpiry, pingPeriodBeforeExpiry);
    }

    public int getMaxKeepAlivePeriod() {
        return (int) getStateHelper().eval(PropertyKeys.maxKeepAlivePeriod, -1);
    }

    public void setMaxKeepAlivePeriod(int maxKeepAlivePeriod) {
        getStateHelper().put(PropertyKeys.maxKeepAlivePeriod, maxKeepAlivePeriod);
    }
}
