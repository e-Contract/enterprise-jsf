/*
 * Enterprise JSF project.
 *
 * Copyright 2021-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

var ejsf = ejsf || {};

(function () {

    ejsf.handleDialogResponse = function (args, dialogWidgetVar, callbackParam) {
        if (args.validationFailed) {
            return;
        }
        if (typeof callbackParam !== "undefined") {
            if (!args[callbackParam]) {
                return;
            }
        }
        if (typeof dialogWidgetVar === "undefined") {
            console.error("cannot close the dialog");
            return;
        }
        PF(dialogWidgetVar).hide();
    };

    ejsf.closeDialog = function (dialogWidgetVar) {
        if (typeof dialogWidgetVar === "undefined") {
            console.error("cannot close the dialog");
            return;
        }
        dialogWidgetVar.split(",").forEach((dialogWidget) => PF(dialogWidget).hide());
    };

    ejsf.openDialog = function (dialogWidgetVar, status, args, whenCallbackParam, whenCallbackParamValue) {
        if (status !== "success") {
            return;
        }
        if (typeof whenCallbackParam !== "undefined") {
            if (whenCallbackParam !== "null") {
                if (typeof whenCallbackParamValue !== "undefined" && whenCallbackParamValue !== "null") {
                    if (args[whenCallbackParam] !== whenCallbackParamValue) {
                        return;
                    }
                } else if (!args[whenCallbackParam]) {
                    return;
                }
            }
        }
        if (typeof dialogWidgetVar === "undefined") {
            console.error("cannot open the dialog");
            return;
        }
        let dialogWidget = PF(dialogWidgetVar);
        if (typeof dialogWidget !== "undefined") {
            dialogWidget.show();
        }
    };

    ejsf.storeDialog = function (event, dialogWidgetVar) {
        event.target.setAttribute("data-dialog", dialogWidgetVar);
    };

    ejsf.handleDialogOnComplete = function (event, status, args, whenCallbackParam, whenCallbackParamValue, whenValid) {
        if (status !== "success") {
            return;
        }
        if (typeof whenValid !== "undefined") {
            if (whenValid !== "null") {
                let valid = whenValid === "true";
                if (valid ? args.validationFailed : !args.validationFailed) {
                    return;
                }
            }
        }
        if (typeof whenCallbackParam !== "undefined") {
            if (whenCallbackParam !== "null") {
                if (typeof whenCallbackParamValue !== "undefined" && whenCallbackParamValue !== "null") {
                    if (args[whenCallbackParam] !== whenCallbackParamValue) {
                        return;
                    }
                } else if (!args[whenCallbackParam]) {
                    return;
                }
            }
        }
        let dialogWidgetVar = event.target.dataset.dialog;
        if (typeof dialogWidgetVar === "undefined") {
            console.error("cannot close the dialog");
            return;
        }
        dialogWidgetVar.split(",").forEach((dialogWidget) => PF(dialogWidget).hide());
    };

    ejsf.copyToClipboard = function (value) {
        navigator.clipboard.writeText(value);
    };

    ejsf.logClientBehavior = function (event, oneventCallback) {
        if (oneventCallback) {
            oneventCallback.call(this, event);
        } else {
            console.log(event.type);
        }
    };

    ejsf.autoSubmit = function (event, whenLength, targetClientId) {
        if (event.target.value.length === whenLength) {
            if (targetClientId) {
                let targetWidget = $(PrimeFaces.escapeClientId(targetClientId));
                targetWidget.trigger(PrimeFaces.csp.clickEvent());
            } else {
                event.target.form.submit();
            }
        }
    };

})();
