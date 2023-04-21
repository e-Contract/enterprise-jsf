/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFSessionKeepAlive = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function (cfg) {
        this._super(cfg);
        let $this = this;
        if (typeof this.cfg.SESSION_KEEP_ALIVE_PING_INTERVAL !== "undefined") {
            console.log("configuring session keep alive ping");
            setInterval(function () {
                $this.keepAlive();
            }, this.cfg.SESSION_KEEP_ALIVE_PING_INTERVAL);
        }
    },

    /**
     * @private
     */
    keepAlive: function () {
        console.log("keep alive ping");
        let ajaxRequestOptions = {
            source: this.id,
            process: this.id,
            async: true,
            global: false
        };
        PrimeFaces.ajax.Request.handle(ajaxRequestOptions);
    }
});