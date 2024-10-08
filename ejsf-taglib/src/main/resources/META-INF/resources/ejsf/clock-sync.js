/*
 * Enterprise JSF project.
 *
 * Copyright 2021-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFClockSync = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function (cfg) {
        this._super(cfg);
        this.syncCount = 0;
        this.bestRoundTripDelay = Number.MAX_SAFE_INTEGER;
        this.inSync = false;

        let $this = this;
        this.syncTimer = setInterval(function () {
            $this.sync();
        }, 1000);

        if (typeof this.cfg.SESSION_KEEP_ALIVE_PING_INTERVAL !== "undefined") {
            console.log("configuring session keep alive ping");
            if (typeof this.cfg.MAX_KEEP_ALIVE_PERIOD !== "undefined") {
                this.cancelTimestamp = Date.now() + this.cfg.MAX_KEEP_ALIVE_PERIOD * 60 * 1000;
            }
            this.keepAliveTimer = setInterval(function () {
                $this.keepAlive();
            }, this.cfg.SESSION_KEEP_ALIVE_PING_INTERVAL);
        }

        this.syncListeners = [];
    },

    /**
     * @private
     */
    syncError: function (errorMessage) {
        console.log("sync error: " + errorMessage);
        if (this.syncTimer) {
            clearInterval(this.syncTimer);
            this.syncTimer = null;
        }
        if (!errorMessage) {
            errorMessage = "unknown error";
        }
        let options = {
            params: [
                {
                    name: this.id + '_errorMessage',
                    value: errorMessage
                }
            ]
        };
        this.callBehavior("error", options);
    },

    /**
     * @private
     */
    sync: function () {
        if (this.syncCount < this.cfg.SYNC_COUNT) {
            let $this = this;
            let xmlHttpRequest = new XMLHttpRequest();
            xmlHttpRequest.onreadystatechange = function () {
                if (this.readyState !== 4) {
                    return;
                }
                if (this.status === 404) {
                    $this.syncError(this.statusText);
                    return;
                }
                if (this.status === 200) {
                    let t1 = Date.now();
                    let roundTripDelay = t1 - $this.t0;
                    console.log("round-trip delay: " + roundTripDelay + " ms");
                    let dt = t1 - this.responseText - roundTripDelay / 2;
                    console.log("dt: " + dt + " ms");
                    if (roundTripDelay < $this.bestRoundTripDelay) {
                        $this.dt = dt;
                        $this.bestRoundTripDelay = roundTripDelay;
                    }
                    $this.syncCount++;
                    if ($this.syncCount === $this.cfg.SYNC_COUNT) {
                        if ($this.bestRoundTripDelay > $this.cfg.ACCEPTED_ROUND_TRIP_DELAY) {
                            // have another round of syncing
                            $this.syncCount = 0;
                        } else {
                            $this.clockDriftCount = 0;
                            $this.inSync = true;
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
                            $this.invokeSyncListeners();
                        }
                    }
                }
            };
            xmlHttpRequest.onerror = function (event) {
                console.log("sync onerror");
                console.log(event);
                $this.syncError("XMLHttpRequest onerror");
            };
            xmlHttpRequest.open("GET", $this.cfg.SYNC_ENDPOINT, true);
            this.t0 = Date.now();
            xmlHttpRequest.send();
        } else if (this.inSync) {
            if (this.clockDriftCount === 0) {
                this.clockDriftTime = Date.now();
            }
            let clockDrift = Math.abs(this.clockDriftTime + this.clockDriftCount * 1000 - Date.now());
            this.clockDriftCount++;
            console.log("clock drift: " + clockDrift + " ms");
            if (clockDrift > this.cfg.MAXIMUM_CLOCK_DRIFT) {
                console.log("clock drift too high: " + clockDrift + " ms");
                this.inSync = false;
                this.syncCount = 0;
                this.bestRoundTripDelay = Number.MAX_SAFE_INTEGER;
            }
        }
    },

    /**
     * Gives back the best estimate for the remaining millisecond until the "event" happens.
     *
     * @param {number} remainingMilliseconds
     * @param {number} expires server-side expires time in milliseconds from the epoch
     * of 1970-01-01T00:00:00Z.
     * @returns {number}
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

    /**
     * Calculates the client-side event time given a server-side event time.
     * If not yet synchronized, we return null.
     * @param {number} serverSideEvent server-side event time in milliseconds from the epoch of 1970-01-01T00:00:00Z.
     * @returns {number|null}
     */
    getClientSideEvent: function (serverSideEvent) {
        if (!this.inSync) {
            return null;
        }
        return serverSideEvent + this.dt;
    },

    /**
     * Returns true if we are synchronized.
     * @returns {Boolean}
     */
    isSynchronized: function () {
        return this.inSync;
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
            global: true,
            event: "keepAlive"
        };
        PrimeFaces.ajax.Request.handle(ajaxRequestOptions);
        if (this.cancelTimestamp) {
            let now = Date.now();
            if (now > this.cancelTimestamp) {
                if (this.keepAliveTimer) {
                    console.log("canceling keep alive timer");
                    clearInterval(this.keepAliveTimer);
                    this.keepAliveTimer = null;
                }
            }
        }
    },

    /**
     * Gets called when we are synchronized.
     *
     * @callback syncCallback
     */

    /**
     * Registers a synchronization callback function.
     *
     * @param {syncCallback} syncCallback the synchronization callback function.
     *
     */
    registerSyncListener: function (syncCallback) {
        this.syncListeners.push(syncCallback);
        if (this.inSync) {
            syncCallback();
        }
    },

    /**
     * Unregisters a synchronization callback function.
     *
     * @param {syncCallback} syncCallback the synchronization callback function.
     *
     */
    unregisterSyncListener: function (syncCallback) {
        this.syncListeners = this.syncListeners.filter(
                function (item) {
                    if (item !== syncCallback) {
                        return item;
                    }
                }
        );
    },

    /**
     * @private
     */
    invokeSyncListeners: function () {
        this.syncListeners.forEach(function (syncCallback) {
            syncCallback();
        });
    },

    destroy: function () {
        this._super();
        if (this.syncTimer) {
            clearInterval(this.syncTimer);
            this.syncTimer = null;
        }
        if (this.keepAliveTimer) {
            clearInterval(this.keepAliveTimer);
            this.keepAliveTimer = null;
        }
    }
});
