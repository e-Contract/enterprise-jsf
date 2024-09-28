/*
 * Enterprise JSF project.
 *
 * Copyright 2014-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFCountdown = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function (cfg) {
        this._super(cfg);
        this.progressBarWidget = PrimeFaces.getWidgetById(this.id + ":progressBar");
        this.progressBarWidgetValue = this.jq.find(this.progressBarWidget.jqId + " .ui-progressbar-value");
        this.clock = this.jq.find(this.jqId + "\\:clock");
        this.clockText = this.jq.find(this.jqId + "\\:clock-text");
        this.counter = this.jq.find(this.jqId + "\\:counter");

        this.expires = 0;

        PrimeFaces.widget.EJSFCountdown.registerCountdown(this);

        if (typeof this.cfg.clockSyncWidgetVar !== "undefined") {
            let clockSyncWidget = PF(this.cfg.clockSyncWidgetVar);
            let $this = this;
            this.clockSyncCallback = function () {
                $this.recalibrate();
            };
            clockSyncWidget.registerSyncListener(this.clockSyncCallback);
        }
    },

    /**
     * @private
     */
    recalibrate: function () {
        console.log("recalibrate");
        if (this.serverSideExpires) {
            let clockSyncWidget = PF(this.cfg.clockSyncWidgetVar);
            let clientSideExpires = clockSyncWidget.getClientSideEvent(this.serverSideExpires);
            if (null !== clientSideExpires) {
                this.expires = clientSideExpires;
            }
        }
    },

    /**
     * Sets the time when the countdown expires.
     * When synchronized via the clockSync component, we can use the server-side time.
     * Else we will use the time left in milliseconds.
     * @param {number} timeLeftInMilliseconds the time left in milliseconds when the countdown expires.
     * @param {number} serverSideExpires the server-side time when the countdown expires.
     */
    setExpires: function (timeLeftInMilliseconds, serverSideExpires) {
        if (typeof this.cfg.clockSyncWidgetVar !== "undefined") {
            this.serverSideExpires = serverSideExpires;
            let clockSyncWidget = PF(this.cfg.clockSyncWidgetVar);
            let clientSideExpires = clockSyncWidget.getClientSideEvent(serverSideExpires);
            if (null !== clientSideExpires) {
                this.expires = clientSideExpires;
                let now = Date.now();
                this.notified = now;
                this.updateCountdown();
                if (this.timer) {
                    window.clearInterval(this.timer);
                    this.timer = null;
                }
                if (!this.cfg.useHeartbeatTimer) {
                    let $this = this;
                    this.timer = window.setInterval(function () {
                        $this.onTimer();
                    }, 1000);
                }
            } else {
                // not yet synchronized, so we use what we have.
                this.setTime(timeLeftInMilliseconds);
            }
        } else {
            this.setTime(timeLeftInMilliseconds);
        }
    },

    /**
     * Sets the time left when the countdown expires.
     * @param {number} timeLeftInMilliseconds the time left in milliseconds when the countdown expires.
     */
    setTime: function (timeLeftInMilliseconds) {
        if (timeLeftInMilliseconds > 0) {
            let now = Date.now();
            this.notified = now;
            this.expires = now + timeLeftInMilliseconds;
            this.updateCountdown();
            if (this.timer) {
                window.clearInterval(this.timer);
                this.timer = null;
            }
            if (!this.cfg.useHeartbeatTimer) {
                let $this = this;
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
        let position = this.jq.get(0).getBoundingClientRect();
        if (position.bottom < -50) {
            return;
        }
        if (position.top > window.innerHeight + 50) {
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

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh: function (cfg) {
        if (this.timer) {
            window.clearInterval(this.timer);
            this.timer = null;
        }
        PrimeFaces.widget.EJSFCountdown.unregisterCountdown(this);
        if (this.clockSyncCallback) {
            let clockSyncWidget = PF(this.cfg.clockSyncWidgetVar);
            clockSyncWidget.unregisterSyncListener(this.clockSyncCallback);
            this.clockSyncCallback = null;
        }
        this._super(cfg);
    },

    /**
     * @private
     */
    heartbeat: function () {
        if (this.cfg.useHeartbeatTimer) {
            this.onTimer();
        }
    }
});

/**
 * Stops all the countdown widgets.
 * @function
 */
PrimeFaces.widget.EJSFCountdown.stopAll = function () {
    PrimeFaces.getWidgetsByType(PrimeFaces.widget.EJSFCountdown).forEach(
            function (countdown) {
                countdown.setTime(0);
            }
    );
};

window.setInterval(function () {
    PrimeFaces.getWidgetsByType(PrimeFaces.widget.EJSFCountdown).forEach(
            function (countdown) {
                countdown.heartbeat();
            }
    );
}, 1000);

PrimeFaces.widget.EJSFCountdown.intersectionObserver = new IntersectionObserver(function (entries) {
    entries.forEach(
            function (entry) {
                let countdownWidget = PrimeFaces.getWidgetById(entry.target.id);
                if (null !== countdownWidget) {
                    countdownWidget.updateCountdown();
                }
            }
    );
});

PrimeFaces.widget.EJSFCountdown.registerCountdown = function (countdown) {
    PrimeFaces.widget.EJSFCountdown.intersectionObserver.observe(countdown.jq.get(0));
};

PrimeFaces.widget.EJSFCountdown.unregisterCountdown = function (countdown) {
    PrimeFaces.widget.EJSFCountdown.intersectionObserver.unobserve(countdown.jq.get(0));
};
