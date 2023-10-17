/*
 * Enterprise JSF project.
 *
 * Copyright 2022-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFDictionary = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function (cfg) {
        this._super(cfg);
    },

    /**
     * Gives back a dictionary message.
     * @param {string} key the key within the dictionary.
     * @returns {string} the corresponding message.
     */
    getMessage: function (key) {
        let message = this.cfg["message_" + key];
        if (typeof message === "undefined") {
            return key;
        }
        return message;
    },

    /**
     * Gives back a formatted message from the dictionary.
     * @param {string} key the key within the dictionary.
     * @param  {...string} args the arguments to format the message.
     * @returns {string} the formatted message.
     */
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
