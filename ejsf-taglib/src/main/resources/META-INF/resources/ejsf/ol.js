/*
 * Enterprise JSF project.
 *
 * Copyright 2025 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFOpenLayers = PrimeFaces.widget.BaseWidget.extend({

    init: function (cfg) {
        this._super(cfg);
        let coord = ol.proj.fromLonLat([this.cfg.longitude, this.cfg.latitude]);
        this.map = new ol.Map({
            target: this.id,
            layers: [
                new ol.layer.Tile({
                    source: new ol.source.OSM({
                        attributions: false
                    })
                }),
                new ol.layer.Vector({
                    source: new ol.source.Vector({
                        features: [
                            new ol.Feature({
                                geometry: new ol.geom.Point(coord)
                            })
                        ]
                    })
                })
            ],
            view: new ol.View({
                center: coord,
                zoom: this.cfg.zoom
            })
        });
    }
});
