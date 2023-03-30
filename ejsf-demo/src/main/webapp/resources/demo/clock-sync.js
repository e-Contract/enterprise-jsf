/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

$(document).ready(function () {
    let clockSyncWidget = PF("clockSync");

    let forbiddenSyncCallback = function () {
        console.error("should not get called");
    };
    clockSyncWidget.registerSyncListener(forbiddenSyncCallback);
    clockSyncWidget.unregisterSyncListener(forbiddenSyncCallback);

    let syncCallback = function () {
        console.log("sync callback invoked");
        clockSyncWidget.unregisterSyncListener(syncCallback);
        clockSyncWidget.registerSyncListener(function () {
            console.log("second sync callback invoked");
        });
    };
    clockSyncWidget.registerSyncListener(syncCallback);
});
