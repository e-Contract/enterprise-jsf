/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named("matomoTrackingController")
@RequestScoped
public class MatomoTrackingController {

    private boolean enableTracking;

    private String matomoUrl;

    private String siteId;

    public boolean isEnableTracking() {
        return this.enableTracking;
    }

    public void setEnableTracking(boolean enableTracking) {
        this.enableTracking = enableTracking;
    }

    public String getMatomoUrl() {
        return this.matomoUrl;
    }

    public void setMatomoUrl(String matomoUrl) {
        this.matomoUrl = matomoUrl;
    }

    public String getSiteId() {
        return this.siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }
}
