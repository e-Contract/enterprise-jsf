/*
 * Enterprise JSF project.
 *
 * Copyright 2024-2025 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFOutputLLM = PrimeFaces.widget.BaseWidget.extend({

    init: function (cfg) {
        this._super(cfg);
        let html = DOMPurify.sanitize(marked.parse(this.cfg.value));
        $(this.jqId).html(html);
    },

    /**
     * Sets the value.
     *
     * @param {string} value
     */
    setValue: function (value) {
        let html = DOMPurify.sanitize(marked.parse(value));
        $(this.jqId).html(html);
    }
});
