/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

function resizeECharts(event) {
    let height = event.target.value;
    let echartsWidget = PF("demoECharts");
    echartsWidget.setHeight(height);
}
