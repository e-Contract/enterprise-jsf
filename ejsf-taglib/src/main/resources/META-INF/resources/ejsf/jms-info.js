/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFJmsInfo = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function (cfg) {
        this._super(cfg);
    },

    /**
     * Called when the JMS message has been replayed.
     * @private
     * @param {string} messageId the JMS message identifier.
     */
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

    /**
     * Called when the JMS message has been removed.
     * @private
     * @param {string} messageId the JMS message identifier.
     */
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