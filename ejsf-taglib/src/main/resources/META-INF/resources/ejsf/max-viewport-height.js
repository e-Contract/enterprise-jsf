/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */


var ejsf = ejsf || {};
(function () {

    ejsf.maxViewportHeight = function () {
        let maxViewportHeightElements = $("[data-ejsf-max-viewport-height]");
        maxViewportHeightElements.each(function () {
            let maxViewportHeightElement = $(this);
            let parentId = maxViewportHeightElement.data("ejsfMaxViewportHeight");
            let parentElement = document.getElementById(parentId);
            let boundingClientRect = parentElement.getBoundingClientRect();
            let top = boundingClientRect.top;
            let viewportHeight = document.documentElement.clientHeight;
            let height = viewportHeight - top;
            let footerId = maxViewportHeightElement.data("ejsfMaxViewportHeightFooter");
            if (footerId) {
                let footerElement = document.getElementById(footerId);
                let footerBoundingClientRect = footerElement.getBoundingClientRect();
                height -= $("#" + footerId).outerHeight(true);
                height -= footerBoundingClientRect.top - boundingClientRect.bottom;
            }
            let minHeight = maxViewportHeightElement.data("ejsfMaxViewportHeightMin");
            if (minHeight) {
                if (height < minHeight) {
                    height = minHeight;
                }
            }
            $("#" + parentId).height(height);
        });
    };
})();

$(document).ready(function () {
    ejsf.maxViewportHeight();
});

window.addEventListener("resize", (event) => {
    ejsf.maxViewportHeight();
});
