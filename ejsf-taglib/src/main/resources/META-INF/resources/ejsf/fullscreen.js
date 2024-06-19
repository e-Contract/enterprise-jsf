/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

var ejsf = ejsf || {};

(function () {

    ejsf.fullscreen = function (id) {
        if (!document.fullscreenEnabled) {
            return;
        }
        if (document.fullscreenElement) {
            document.exitFullscreen();
            return;
        }
        let element = document.getElementById(id);
        element.requestFullscreen().catch((err) => {
            console.log("error going fullscreen on " + id);
            console.log(err);
        });
    };

})();
