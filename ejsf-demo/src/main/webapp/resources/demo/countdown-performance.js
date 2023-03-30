/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

function countdownStartAll() {
    let now = Date.now();
    let timeInMilliseconds = 60 * 1000 + 10 * 1000;
    let expires = now + timeInMilliseconds;
    PrimeFaces.getWidgetsByType(PrimeFaces.widget.EJSFCountdown).forEach(
            function (countdown) {
                countdown.setExpires(timeInMilliseconds, expires);
            }
    );
}
