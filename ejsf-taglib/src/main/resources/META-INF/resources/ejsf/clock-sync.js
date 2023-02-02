/*
 * Enterprise JSF project.
 *
 * Copyright 2021-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFClockSync = PrimeFaces.widget.BaseWidget.extend({
    init: function (cfg) {
        this._super(cfg);
        this.syncCount = 0;
        this.bestRoundTripDelay = Number.MAX_SAFE_INTEGER;
        this.inSync = false;

        let $this = this;
        setInterval(function () {
            $this.sync();
        }, 1000);

        if (typeof this.cfg.SESSION_KEEP_ALIVE_PING_INTERVAL !== "undefined") {
            console.log("configuring session keep alive ping");
            setInterval(function () {
                $this.keepAlive();
            }, this.cfg.SESSION_KEEP_ALIVE_PING_INTERVAL);
        }
    },

    sync: function () {
        if (this.syncCount < this.cfg.SYNC_COUNT) {
            let $this = this;
            let xmlHttpRequest = new XMLHttpRequest();
            xmlHttpRequest.onreadystatechange = function () {
                if (this.readyState === 4 && this.status === 200) {
                    let t1 = Date.now();
                    let roundTripDelay = t1 - $this.t0;
                    console.log("round-trip delay: " + roundTripDelay + " ms");
                    console.log("response: " + this.responseText);
                    let dt = t1 - this.responseText - roundTripDelay / 2;
                    console.log("dt: " + dt);
                    if (roundTripDelay < $this.bestRoundTripDelay) {
                        $this.dt = dt;
                        $this.bestRoundTripDelay = roundTripDelay;
                        if (roundTripDelay < $this.cfg.ACCEPTED_ROUND_TRIP_DELAY) {
                            // already good enough
                            $this.inSync = true;
                        }
                    }
                    $this.syncCount++;
                    if ($this.syncCount === $this.cfg.SYNC_COUNT) {
                        console.log("best round-trip delay: " + $this.bestRoundTripDelay + " ms");
                        console.log("corresponding dt: " + $this.dt + " ms");
                        let options = {
                            params: [
                                {
                                    name: $this.id + '_bestRoundTripDelay',
                                    value: $this.bestRoundTripDelay
                                },
                                {
                                    name: $this.id + '_deltaT',
                                    value: Math.round($this.dt)
                                }
                            ]
                        };
                        $this.callBehavior("sync", options);
                        $this.clockDriftCount = 0;
                    }
                }
            };
            xmlHttpRequest.open("GET", $this.cfg.SYNC_ENDPOINT, true);
            this.t0 = Date.now();
            xmlHttpRequest.send();
        } else {
            if (this.clockDriftCount === 0) {
                this.clockDriftTime = Date.now();
            }
            let clockDrift = Math.abs(this.clockDriftTime + this.clockDriftCount * 1000 - Date.now());
            this.clockDriftCount++;
            console.log("clock drift: " + clockDrift);
            if (clockDrift > this.cfg.MAXIMUM_CLOCK_DRIFT) {
                console.log("clock drift too high: " + clockDrift);
                this.inSync = false;
                this.syncCount = 0;
            }
        }
    },

    /**
     * Gives back the best estimate for the remaining millisecond until the "event" happens.
     * 
     * @param {type} remainingMilliseconds
     * @param {type} expires server-side expires time in milliseconds from the epoch
     * of 1970-01-01T00:00:00Z..
     * @returns {Number}
     */
    getBestRemainingMilliseconds: function (remainingMilliseconds, expires) {
        if (remainingMilliseconds === 0) {
            return 0;
        }
        if (!this.inSync) {
            console.log("not in sync");
            return remainingMilliseconds;
        }
        let t = Date.now();
        let bestRemainingMilliseconds = expires - t + this.dt;
        console.log("original remaining seconds: " + remainingMilliseconds);
        console.log("best remaining seconds: " + bestRemainingMilliseconds);
        return bestRemainingMilliseconds;
    },

    keepAlive: function () {
        console.log("keep alive ping");
        let xmlHttpRequest = new XMLHttpRequest();
        xmlHttpRequest.open("GET", this.cfg.SYNC_ENDPOINT, true);
        xmlHttpRequest.send();
    }
});
