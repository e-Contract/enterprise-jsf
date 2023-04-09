/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFGeolocation = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function (cfg) {
        this._super(cfg);
        if (typeof this.cfg.AUTO_START !== "undefined") {
            if (this.cfg.AUTO_START === "true") {
                this.currentPosition();
            }
        }
    },

    /**
     * Retrieve the current position.
     */
    currentPosition: function () {
        let $this = this;
        let options = {};
        if (typeof this.cfg.MAXIMUM_AGE !== "undefined") {
            options.maximumAge = parseInt(this.cfg.MAXIMUM_AGE);
        }
        if (typeof this.cfg.TIMEOUT !== "undefined") {
            options.timeout = parseInt(this.cfg.TIMEOUT);
        }
        if (typeof this.cfg.HIGH_ACCURACY !== "undefined") {
            options.enableHighAccuracy = this.cfg.HIGH_ACCURACY === "true";
        }
        navigator.geolocation.getCurrentPosition(function (position) {
            $this.onSuccess(position);
        }, function (error) {
            $this.onError(error);
        }, options);
    },

    /**
     * @private
     * @param {any} position
     */
    onSuccess: function (position) {
        let coords = position.coords;
        var options = {
            params: [
                {
                    name: this.id + "_latitude",
                    value: coords.latitude
                },
                {
                    name: this.id + "_longitude",
                    value: coords.longitude
                },
                {
                    name: this.id + "_accuracy",
                    value: coords.accuracy
                }
            ]
        };
        this.callBehavior("geolocation", options);
    },

    /**
     * @private
     * @param {any} error
     */
    onError: function (error) {
        console.log("error code: " + error.code);
        console.log("error message: " + error.message);
        var options = {
            params: [
                {
                    name: this.id + "_error_code",
                    value: error.code
                },
                {
                    name: this.id + "_error_message",
                    value: error.message
                }
            ]
        };
        this.callBehavior("error", options);
    }
});
