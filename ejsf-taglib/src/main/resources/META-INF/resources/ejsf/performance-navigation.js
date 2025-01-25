/*
 * Enterprise JSF project.
 *
 * Copyright 2024-2025 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFPerformanceNavigation = PrimeFaces.widget.BaseWidget.extend({

    init: function (cfg) {
        this._super(cfg);
        if (!("performance" in window)) {
            return;
        }
        let $this = this;
        this.observer = new PerformanceObserver(function (list) {
            $this.observerCallback(list);
        });
        this.observer.observe({
            type: "navigation",
            buffered: true
        });
    },

    observerCallback: function (list) {
        this.observer.disconnect();
        let entry = list.getEntries()[0];
        let options = {
            params: [
                {
                    name: this.id + "_startTime",
                    value: entry.startTime
                },
                {
                    name: this.id + "_duration",
                    value: entry.duration
                },
                {
                    name: this.id + "_loadEventStart",
                    value: entry.loadEventStart
                },
                {
                    name: this.id + "_loadEventEnd",
                    value: entry.loadEventEnd
                },
                {
                    name: this.id + "_domInteractive",
                    value: entry.domInteractive
                },
                {
                    name: this.id + "_domComplete",
                    value: entry.domComplete
                },
                {
                    name: this.id + "_requestStart",
                    value: entry.requestStart
                },
                {
                    name: this.id + "_responseStart",
                    value: entry.responseStart
                },
                {
                    name: this.id + "_responseEnd",
                    value: entry.responseEnd
                },
                {
                    name: this.id + "_name",
                    value: entry.name
                }
            ]
        };
        this.callBehavior("timing", options);
    }
});
