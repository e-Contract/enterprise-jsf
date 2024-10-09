/*
 * Enterprise JSF project.
 *
 * Copyright 2023-2024 e-Contract.be BV. All rights reserved.
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
        if (typeof this.cfg.SESSION_KEEP_ALIVE_PING_INTERVAL === "undefined") {
            return;
        }
        if (typeof this.cfg.MAX_KEEP_ALIVE_PERIOD !== "undefined") {
            this.cancelTimestamp = Date.now() + this.cfg.MAX_KEEP_ALIVE_PERIOD * 60 * 1000;
        }
        let $this = this;
        this.timer = setInterval(function () {
            $this.keepAlive();
        }, this.cfg.SESSION_KEEP_ALIVE_PING_INTERVAL);
    },

    /**
     * @private
     */
    keepAlive: function () {
        let ajaxRequestOptions = {
            source: this.id,
            process: this.id,
            async: true,
            global: true,
            event: "keepAlive"
        };
        PrimeFaces.ajax.Request.handle(ajaxRequestOptions);
        if (this.cancelTimestamp) {
            let now = Date.now();
            if (now > this.cancelTimestamp) {
                if (this.timer) {
                    console.log("canceling keep alive timer");
                    clearInterval(this.timer);
                    this.timer = null;
                }
            }
        }
    }
});
