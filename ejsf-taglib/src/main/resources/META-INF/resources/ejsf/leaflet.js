/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFLeaflet = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function (cfg) {
        this._super(cfg);
        let latitude = this.cfg.latitude;
        let longitude = this.cfg.longitude;
        let leafletMap = L.map(this.id, {
            zoomControl: this.cfg.zoomControl
        }).setView([latitude, longitude], 13);
        L.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png", {
            maxZoom: 19,
            attribution: "&copy; <a href='http://www.openstreetmap.org/copyright' target='_blank'>OpenStreetMap</a>"
        }).addTo(leafletMap);
        let $this = this;
        L.Icon.Default.prototype._getIconUrl = function (name) {
            console.log("_getIconUrl: " + name);
            let iconUrl = $this.cfg[name + "_request_path"];
            console.log("result: " + iconUrl);
            return iconUrl;
        };
        L.marker([latitude, longitude]).addTo(leafletMap);
    }
});
