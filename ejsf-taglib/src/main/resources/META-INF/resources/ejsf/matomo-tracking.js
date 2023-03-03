/*
 * Enterprise JSF project.
 *
 * Copyright 2022-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFMatomoTracking = PrimeFaces.widget.BaseWidget.extend({
    init: function (cfg) {
        this._super(cfg);
        let _cfg = this.cfg;
        let _paq = window._paq = window._paq || [];
        _paq.push(["trackPageView"]);
        _paq.push(["enableLinkTracking"]);
        (function () {
            let url = _cfg.MATOMO_URL + "/";
            _paq.push(["setTrackerUrl", url + "matomo.php"]);
            _paq.push(["setSiteId", _cfg.SITE_ID]);
            if (typeof _cfg.USER_ID !== "undefined") {
                _paq.push(["setUserId", _cfg.USER_ID]);
            }
            if (typeof _cfg.DOCUMENT_TITLE !== "undefined") {
                _paq.push(["setDocumentTitle", _cfg.DOCUMENT_TITLE]);
            }
            let matomoScript = document.createElement("script");
            matomoScript.type = "text/javascript";
            matomoScript.async = true;
            matomoScript.src = url + "matomo.js";
            let firstScript = document.getElementsByTagName("script")[0];
            firstScript.parentNode.insertBefore(matomoScript, firstScript);
        })();
    }
});
