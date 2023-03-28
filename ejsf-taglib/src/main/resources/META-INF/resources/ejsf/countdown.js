/*
 * Enterprise JSF project.
 *
 * Copyright 2014-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFCountdown = PrimeFaces.widget.BaseWidget.extend({
    init: function (cfg) {
        this._super(cfg);
        this.progressBarWidget = PrimeFaces.getWidgetById(this.id + ":progressBar");
        this.progressBarWidgetValue = this.jq.find(this.progressBarWidget.jqId + " .ui-progressbar-value");
        this.clock = this.jq.find(this.jqId + "\\:clock");
        this.clockText = this.jq.find(this.jqId + "\\:clock-text");
        this.counter = this.jq.find(this.jqId + "\\:counter");

        this.expires = 0;

        let $this = this;
        this.intersectionObserver = new IntersectionObserver(function () {
            $this.updateCountdown();
        }, {
            threshold: 1.0
        });
        this.intersectionObserver.observe(this.jq.get(0));
    },

    setTime: function (timeInMilliseconds) {
        if (timeInMilliseconds > 0) {
            let now = Date.now();
            this.notified = now;
            this.expires = now + timeInMilliseconds;
            this.updateCountdown();
            if (this.timer) {
                window.clearInterval(this.timer);
            }
            let $this = this;
            if (!this.cfg.useHeartbeatTimer) {
                this.timer = window.setInterval(function () {
                    $this.onTimer();
                }, 1000);
            }
        } else {
            this.notified = 0;
            this.expires = 0;
            this.updateCountdown();
            if (this.timer) {
                window.clearInterval(this.timer);
                this.timer = null;
            }
        }
    },

    /**
     * @private
     */
    onTimer: function () {
        let now = Date.now();
        let remainingMilliseconds = this.expires - now;
        if (remainingMilliseconds <= 0) {
            if (this.timer) {
                window.clearInterval(this.timer);
                this.timer = null;
            }
            this.updateCountdown();
            return;
        }
        let position = this.progressBarWidget.jq.get(0).getBoundingClientRect();
        if (position.bottom < 0) {
            return;
        }
        if (position.top > window.innerHeight) {
            return;
        }
        this.updateCountdown();
    },

    /**
     * @private
     */
    updateCountdown: function () {
        let now = Date.now();
        let remainingMilliseconds = this.expires - now;
        let timeInMilliseconds = this.expires - this.notified;
        let counterValue;
        let progressBarValueClasses = "ui-progressbar-value ui-widget-header ui-corner-all";
        if (remainingMilliseconds <= 0) {
            counterValue = "";
            this.clock.css("display", "none");
            this.progressBarWidget.setValue(0);
        } else {
            this.progressBarWidget.setValue(100 * remainingMilliseconds / timeInMilliseconds);
            if (remainingMilliseconds <= 10 * 1000) {
                this.clock.css("display", "none");
                progressBarValueClasses = "progress-10-seconds " + progressBarValueClasses;
                counterValue = Math.round(remainingMilliseconds / 1000);
            } else {
                counterValue = "";
                this.clock.css("display", "inline");
                let remainingDays = Math.floor(remainingMilliseconds / (1000 * 60 * 60 * 24));
                let remainingSeconds = Math.round(remainingMilliseconds / 1000);
                let clockText = ('0' + Math.floor(remainingSeconds / 3600) % 24).slice(-2) + ':' + ('0' + Math.floor(remainingSeconds / 60) % 60).slice(-2) + ':' + ('0' + remainingSeconds % 60).slice(-2);
                if (remainingDays > 1) {
                    clockText = remainingDays + " " + this.cfg.daysAnd + " " + clockText;
                } else if (remainingDays === 1) {
                    clockText = remainingDays + " " + this.cfg.dayAnd + " " + clockText;
                }
                this.clockText.text(clockText);
                if (remainingMilliseconds <= 60 * 1000) {
                    progressBarValueClasses = "progress-1-minute " + progressBarValueClasses;
                } else {
                    progressBarValueClasses = "progress-normal " + progressBarValueClasses;
                }
            }
        }
        this.counter.text(counterValue);
        this.progressBarWidgetValue.attr("class", progressBarValueClasses);
    },

    refresh: function (cfg) {
        if (this.timer) {
            window.clearInterval(this.timer);
            this.timer = null;
        }
        this._super(cfg);
    },

    heartbeat: function () {
        if (this.cfg.useHeartbeatTimer) {
            this.onTimer();
        }
    }
});

PrimeFaces.widget.EJSFCountdown.stopAll = function () {
    PrimeFaces.getWidgetsByType(PrimeFaces.widget.EJSFCountdown).forEach(
            function (countdown) {
                countdown.setTime(0);
            }
    );
};

PrimeFaces.widget.EJSFCountdown.heartbeat = function () {
    PrimeFaces.getWidgetsByType(PrimeFaces.widget.EJSFCountdown).forEach(
            function (countdown) {
                countdown.heartbeat();
            }
    );
};

window.setInterval(function () {
    PrimeFaces.widget.EJSFCountdown.heartbeat();
}, 1000);