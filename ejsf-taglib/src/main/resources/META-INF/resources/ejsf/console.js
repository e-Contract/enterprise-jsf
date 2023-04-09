/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFConsole = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function (cfg) {
        this._super(cfg);
        if (typeof this.cfg.timestamp !== "undefined") {
            this.timestamp = this.cfg.timestamp;
        } else {
            this.timestamp = false;
        }
    },

    /**
     * Adds an info message to the console.
     * @param {string} message the info message.
     */
    info: function (message) {
        this.addMessage(message, "info");
    },

    /**
     * Adds an error message to the console.
     * @param {string} message the error message.
     */
    error: function (message) {
        this.addMessage(message, "error");
    },

    /**
     * Adds a message to the console.
     *
     * @private
     * @param {string} message the message.
     * @param {string} styleClass the message style class. "info" or "error".
     */
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

    /**
     * Gives back a formatted timestamp.
     * @private
     * @returns a formatted timestamp.
     */
    getTimestamp: function () {
        let now = new Date();
        let timestamp = ("0" + now.getHours()).slice(-2) + ":" +
                ("0" + now.getMinutes()).slice(-2) + ":" +
                ("0" + now.getSeconds()).slice(-2);
        return timestamp;
    },

    /**
     * Clears all the messages from the console.
     */
    clear: function () {
        $(this.jqId).children("div").remove();
    }
});
