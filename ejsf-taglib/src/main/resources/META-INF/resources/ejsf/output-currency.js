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
    },

    /**
     * Sets the currency (formatted value).
     * @param {string} value the formatted value.
     */
    setValue: function (value) {
        this.currencyValue = value;
        if (null !== value) {
            let formattedValue = this.formatValue(value);
            $(this.jqId + "\\:value").text(formattedValue);
            $(this.jqId + "\\:currency").text(" " + this.currency);
        } else {
            $(this.jqId + "\\:value").text("");
            $(this.jqId + "\\:currency").text("");
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
     * Gives back the formatted currency value.
     * @returns {string} the formatted currency.
     */
    getValue: function () {
        return this.currencyValue;
    }
});
