/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFGeolocation = PrimeFaces.widget.BaseWidget.extend({
    init: function (cfg) {
        this._super(cfg);
    },

    currentPosition: function () {
        let $this = this;
        navigator.geolocation.getCurrentPosition(function (position) {
            $this.onSuccess(position);
        }, function (error) {
            $this.onError(error);
        });
    },

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
