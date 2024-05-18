/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

$(document).ready(function () {
    function sameHeight(name) {
        let sameHeightElements = $("[data-ejsf-same-height='" + name + "']");
        let maxHeight = 0;
        sameHeightElements.each(function () {
            let sameHeightElement = $(this);
            let parentElement = sameHeightElement.parent();
            let parentHeight = parentElement.height();
            if (parentHeight > maxHeight) {
                maxHeight = parentHeight;
            }
        });
        sameHeightElements.each(function () {
            let sameHeightElement = $(this);
            let parentElement = sameHeightElement.parent();
            parentElement.height(maxHeight);
        });
    }

    let sameHeightElements = $("[data-ejsf-same-height]");
    let names = new Set();
    sameHeightElements.each(function () {
        let sameHeightElement = $(this);
        let name = sameHeightElement.data("ejsf-same-height");
        names.add(name);
    });
    for (const name of names) {
        sameHeight(name);
    }
});
