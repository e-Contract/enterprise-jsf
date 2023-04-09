/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFD3 = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function (cfg) {
        this._super(cfg);
        this.d3 = d3.select(document.getElementById(this.id));
    },

    /**
     * Gives back the d3 instance of this widget.
     * @returns the d3 instance.
     */
    getD3: function () {
        return this.d3;
    },

    /**
     * Gives back the width of the widget.
     * @returns {number} the width.
     */
    getWidth: function () {
        return this.cfg.WIDTH;
    },

    /**
     * Gives back the height of the widget.
     * @returns {number} the height.
     */
    getHeight: function () {
        return this.cfg.HEIGHT;
    }
});
