/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFD3 = PrimeFaces.widget.BaseWidget.extend({
    init: function (cfg) {
        this._super(cfg);
        this.d3 = d3.select(document.getElementById(this.id));
    },

    getD3: function () {
        return this.d3;
    },

    getWidth: function () {
        return this.cfg.WIDTH;
    },

    getHeight: function () {
        return this.cfg.HEIGHT;
    }
});
