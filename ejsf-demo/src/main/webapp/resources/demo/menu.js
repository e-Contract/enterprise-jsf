/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

function selectMenuitemLink(link) {
    $("#mainMenuList").find(".ui-state-active").removeClass("ui-state-active");
    $(link).addClass("ui-state-active");
}
