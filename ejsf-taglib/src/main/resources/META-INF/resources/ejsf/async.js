/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

$(document).ready(function () {
    let asyncElements = $("[data-ejsf-async]");
    asyncElements.each(function () {
        let asyncElement = $(this);
        let asyncElementId = asyncElement.attr("id");
        setTimeout(function () {
            let ajaxRequestOptions = {
                source: asyncElementId,
                process: "@form",
                event: "loadData",
                async: true
            };
            PrimeFaces.ajax.Request.handle(ajaxRequestOptions);
        }, 0);
    });
});
