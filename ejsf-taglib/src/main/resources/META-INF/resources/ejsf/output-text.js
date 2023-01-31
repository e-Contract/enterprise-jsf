/*
 * Enterprise JSF project.
 *
 * Copyright 2022-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFOutputText = PrimeFaces.widget.BaseWidget.extend({
    init: function (cfg) {
        this._super(cfg);
        if (typeof this.cfg.initialValue !== "undefined") {
            this.value = this.cfg.initialValue;
        } else {
            this.value = null;
        }
    },

    setValue: function (value) {
        this.value = value;
        if (null !== value) {
            $(this.jqId).text(value);
        } else {
            $(this.jqId).text("");
        }
    },

    getValue: function () {
        return this.value;
    }
});
