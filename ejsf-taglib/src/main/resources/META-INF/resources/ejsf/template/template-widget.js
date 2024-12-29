/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFInputTemplate = PrimeFaces.widget.BaseWidget.extend({

    init: function (cfg) {
        this._super(cfg);

        let element = $(this.jqId + $.escapeSelector(":content")).get(0);
        let template = this.jq.find("input[type=hidden]").val();
        if (null === template) {
            return;
        }
        if (template.length === 0) {
            return;
        }
        let $this = this;
        let ejsfTemplate = new EJSFTemplate(element, template, function (result) {
            $this.resultCallback(result);
        });
        ejsfTemplate.init();
    },

    resultCallback(result) {
        this.jq.find("input[type=hidden]").val(result);
    }
});
