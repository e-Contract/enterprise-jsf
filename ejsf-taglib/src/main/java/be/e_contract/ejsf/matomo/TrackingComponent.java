/*
 * Enterprise JSF project.
 *
 * Copyright 2022-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.matomo;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import org.primefaces.component.api.Widget;

@FacesComponent(TrackingComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "ejsf", name = "matomo-tracking.js")
})
public class TrackingComponent extends UIComponentBase implements Widget {

    public static final String COMPONENT_TYPE = "ejsf.matomoTrackingComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        enableTracking,
        matomoUrl,
        siteId,
    }

    public boolean isEnableTracking() {
        return (Boolean) getStateHelper().eval(PropertyKeys.enableTracking, false);
    }

    public void setEnableTracking(boolean enableTracking) {
        getStateHelper().put(PropertyKeys.enableTracking, enableTracking);
    }

    public String getMatomoUrl() {
        return (String) getStateHelper().eval(PropertyKeys.matomoUrl);
    }

    public void setMatomoUrl(String matomoUrl) {
        getStateHelper().put(PropertyKeys.matomoUrl, matomoUrl);
    }

    public String getSiteId() {
        return (String) getStateHelper().eval(PropertyKeys.siteId);
    }

    public void setSiteId(String siteId) {
        getStateHelper().put(PropertyKeys.siteId, siteId);
    }
}
