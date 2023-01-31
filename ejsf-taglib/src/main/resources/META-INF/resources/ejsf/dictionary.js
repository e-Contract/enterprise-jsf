/*
 * Enterprise JSF project.
 *
 * Copyright 2022-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFDictionary = PrimeFaces.widget.BaseWidget.extend({
    init: function (cfg) {
        this._super(cfg);
    },

    getMessage: function (key) {
        let message = this.cfg["message_" + key];
        if (typeof message === "undefined") {
            return key;
        }
        return message;
    },

    getFormattedMessage: function (key, ...args) {
        let message = this.cfg["message_" + key];
        if (typeof message === "undefined") {
            return key;
        }
        for (let idx = 0; idx < args.length; idx++) {
            var regExp = new RegExp('\\{' + idx + '\\}', 'gm');
            message = message.replace(regExp, args[idx]);
        }
        return message;
    }
});

