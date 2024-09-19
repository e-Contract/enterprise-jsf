/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFPerformanceNavigation = PrimeFaces.widget.BaseWidget.extend({

    init: function (cfg) {
        this._super(cfg);
        if (!("performance" in window)) {
            return;
        }
        this.entry = window.performance.getEntriesByType("navigation")[0];
        let $this = this;
        this.timer = window.setInterval(function () {
            $this.onTimer();
        }, 1000);
    },

    onTimer: function () {
        if (this.entry.duration === 0) {
            return;
        }
        window.clearInterval(this.timer);
        this.timer = null;
        let options = {
            params: [
                {
                    name: this.id + "_startTime",
                    value: this.entry.startTime
                },
                {
                    name: this.id + "_duration",
                    value: this.entry.duration
                },
                {
                    name: this.id + "_loadEventStart",
                    value: this.entry.loadEventStart
                },
                {
                    name: this.id + "_loadEventEnd",
                    value: this.entry.loadEventEnd
                },
                {
                    name: this.id + "_domInteractive",
                    value: this.entry.domInteractive
                },
                {
                    name: this.id + "_domComplete",
                    value: this.entry.domComplete
                },
                {
                    name: this.id + "_requestStart",
                    value: this.entry.requestStart
                },
                {
                    name: this.id + "_responseStart",
                    value: this.entry.responseStart
                },
                {
                    name: this.id + "_responseEnd",
                    value: this.entry.responseEnd
                },
                {
                    name: this.id + "_name",
                    value: this.entry.name
                }
            ]
        };
        this.callBehavior("timing", options);
    }
});
