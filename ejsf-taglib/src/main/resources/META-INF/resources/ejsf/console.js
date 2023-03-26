/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFConsole = PrimeFaces.widget.BaseWidget.extend({
    init: function (cfg) {
        this._super(cfg);
        if (typeof this.cfg.timestamp !== "undefined") {
            this.timestamp = this.cfg.timestamp;
        } else {
            this.timestamp = false;
        }
    },

    info: function (message) {
        this.addMessage(message, "info");
    },

    error: function (message) {
        this.addMessage(message, "error");
    },

    addMessage: function (message, styleClass) {
        let displayMessage;
        if (this.timestamp) {
            let timestamp = this.getTimestamp();
            displayMessage = timestamp + " " + message;
        } else {
            displayMessage = message;
        }
        let divElement = document.createElement("div");
        $(divElement).text(displayMessage);
        $(divElement).addClass(styleClass);
        $(this.jqId).append(divElement);
        let scrollHeight = $(this.jqId).prop("scrollHeight");
        $(this.jqId).scrollTop(scrollHeight);
    },

    getTimestamp: function () {
        let now = new Date();
        let timestamp = ("0" + now.getHours()).slice(-2) + ":" +
                ("0" + now.getMinutes()).slice(-2) + ":" +
                ("0" + now.getSeconds()).slice(-2);
        return timestamp;
    },

    clear: function () {
        $(this.jqId).children("div").remove();
    }
});
