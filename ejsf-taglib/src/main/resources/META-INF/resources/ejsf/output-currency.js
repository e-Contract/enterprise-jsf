/*
 * Enterprise JSF project.
 *
 * Copyright 2020-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFOutputCurrency = PrimeFaces.widget.BaseWidget.extend({
    init: function (cfg) {
        this._super(cfg);
        if (typeof this.cfg.initialValue !== "undefined") {
            this.currencyValue = this.cfg.initialValue;
        } else {
            this.currencyValue = null;
        }
        this.currency = this.cfg.currency;
    },

    setValue: function (value) {
        this.currencyValue = value;
        if (null !== value) {
            $(this.jqId + "\\:value").text(value);
            $(this.jqId + "\\:currency").text(" " + this.currency);
        } else {
            $(this.jqId + "\\:value").text("");
            $(this.jqId + "\\:currency").text("");
        }
    },

    getValue: function () {
        return this.currencyValue;
    }
});
