/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFJmsInfo = PrimeFaces.widget.BaseWidget.extend({
    init: function (cfg) {
        this._super(cfg);
    },

    messageReplayed: function (messageId) {
        let replayBehaviorOptions = {
            params: [
                {
                    name: this.id + "_messageId",
                    value: messageId
                }
            ]
        };
        this.callBehavior("replay", replayBehaviorOptions);
    },

    messageRemoved: function (messageId) {
        let removeBehaviorOptions = {
            params: [
                {
                    name: this.id + "_messageId",
                    value: messageId
                }
            ]
        };
        this.callBehavior("remove", removeBehaviorOptions);
    }
});