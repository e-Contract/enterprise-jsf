/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFTestComponent = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function (cfg) {
        this._super(cfg);
        this.console = PrimeFaces.getWidgetById(this.id + ":console");
        this.log("Start testing component...");
        this.requestTest();
    },

    /**
     * @private
     */
    requestTest: function () {
        let $this = this;
        let ajaxRequestOptions = {
            source: this.id,
            process: this.id,
            async: true,
            global: false,
            params: [
                {
                    name: this.id + "_request_test",
                    value: true
                }
            ],
            oncomplete: function (xhr, status, args, data) {
                let testName = args.name;
                if (typeof testName === "undefined") {
                    $this.log("Done testing.");
                    return;
                }
                $this.log("Completed test: " + testName);
                let testResult = args.result;
                if (typeof testResult !== "undefined") {
                    let resultLines = testResult.split("\n");
                    resultLines.forEach((resultLine) => {
                        $this.log(resultLine);
                    });
                }
                $this.requestTest();
            }
        };
        PrimeFaces.ajax.Request.handle(ajaxRequestOptions);
    },

    /**
     * Called to indicate that a test has failed.
     */
    failedTest: function (message) {
        this.console.error("FAILED TEST DETECTED: " + message);
    },

    /**
     * @private
     */
    log: function (message) {
        this.console.info(message);
    }
});
