/*
 * Enterprise JSF project.
 *
 * Copyright 2021-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

var ejsf = ejsf || {};

(function () {

    ejsf.handleDialogResponse = function (args, dailogWidgetVar, callbackParam) {
        if (args.validationFailed) {
            return;
        }
        if (typeof callbackParam !== "undefined") {
            if (!args[callbackParam]) {
                return;
            }
        }
        PF(dailogWidgetVar).hide();
    };

    ejsf.closeDialog = function (dialogWidgetVar) {
        PF(dialogWidgetVar).hide();
    };

    ejsf.storeDialog = function (event, dialogWidgetVar) {
        event.target.setAttribute('data-dialog', dialogWidgetVar);
    };

    ejsf.handleDialogOnComplete = function (event, status, args, whenCallbackParam) {
        if (status !== "success") {
            return;
        }
        if (!args[whenCallbackParam]) {
            return;
        }
        let dialogWidgetVar = event.target.dataset.dialog;
        PF(dialogWidgetVar).hide();
    };

})();
