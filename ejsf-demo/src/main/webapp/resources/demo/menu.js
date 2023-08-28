/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

function selectMenuitemLink(link) {
    $("#menuForm\\:mainMenuList").find(".ui-state-active").removeClass("ui-state-active");
    $(link).addClass("ui-state-active");
}

$(document).ready(function () {
    let activeStateMenuItemElements = $("#menuForm\\:mainMenuList").find(".ui-state-active");
    if (activeStateMenuItemElements.length === 1) {
        activeStateMenuItemElements[0].scrollIntoView({
            behavior: "instant"
        });
    }
    let headerElements = $("#header");
    headerElements[0].scrollIntoView({
        behavior: "instant"
    });
});
