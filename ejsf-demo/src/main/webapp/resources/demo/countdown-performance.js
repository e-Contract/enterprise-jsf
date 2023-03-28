/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

function countdownStartAll(time) {
    PrimeFaces.getWidgetsByType(PrimeFaces.widget.EJSFCountdown).forEach(
            function (countdown) {
                countdown.setTime(time);
            }
    );
}
