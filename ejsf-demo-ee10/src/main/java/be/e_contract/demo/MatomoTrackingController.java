/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named("matomoTrackingController")
@RequestScoped
public class MatomoTrackingController {

    private boolean enableTracking;

    private String matomoUrl;

    private String siteId;

    private String userId;

    private String documentTitle;

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

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDocumentTitle() {
        return this.documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }
}
