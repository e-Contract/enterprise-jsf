/*
 * Enterprise JSF project.
 *
 * Copyright 2020-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFOutputCurrency = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function (cfg) {
        this._super(cfg);
        if (typeof this.cfg.initialValue !== "undefined") {
            this.currencyValue = this.cfg.initialValue;
        } else {
            this.currencyValue = null;
        }
        this.currency = this.cfg.currency;
        this.jqValue = $(this.jqId + "\\:value");
        this.jqCurrency = $(this.jqId + "\\:currency");
    },

    /**
     * Sets the currency value.
     * @param {number} value the value.
     */
    setValue: function (value) {
        if (null !== value) {
            this.currencyValue = Number(value);
            let formattedValue = this.formatValue(value);
            this.jqValue.text(formattedValue);
            this.jqCurrency.text(" " + this.currency);
        } else {
            this.currencyValue = null;
            this.jqValue.text("");
            this.jqCurrency.text("");
        }
    },

    /**
     * Formats the currency value.
     * @private
     * @param {number} value the currency value.
     * @returns {string} the formatted value.
     */
    formatValue: function (value) {
        let numberFormat = Intl.NumberFormat(this.cfg.locale, {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        });
        let formattedValue = numberFormat.format(value);
        return formattedValue;
    },

    /**
     * Gives back the currency value.
     * @returns {number} the currency value.
     */
    getValue: function () {
        return this.currencyValue;
    }
});
