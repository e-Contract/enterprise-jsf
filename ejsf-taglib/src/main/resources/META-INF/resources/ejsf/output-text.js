/*
 * Enterprise JSF project.
 *
 * Copyright 2022-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFOutputText = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function (cfg) {
        this._super(cfg);
        if (typeof this.cfg.initialValue !== "undefined") {
            this.value = this.cfg.initialValue;
        } else {
            this.value = null;
        }
    },

    /**
     * Sets the value.
     *
     * @param {string} value
     */
    setValue: function (value) {
        this.value = value;
        if (null !== value) {
            if (typeof this.cfg.unit !== "undefined") {
                $(this.jqId).text(value + " " + this.cfg.unit);
            } else {
                $(this.jqId).text(value);
            }
        } else {
            $(this.jqId).text("");
        }
    },

    /**
     * Gives back the value.
     * @returns {string} the value.
     */
    getValue: function () {
        return this.value;
    }
});
