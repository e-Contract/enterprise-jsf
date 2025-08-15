/*
 * Enterprise JSF project.
 *
 * Copyright 2023-2025 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

function resizeECharts(event) {
    let height = event.target.value;
    let echartsWidget = PF("demoECharts");
    echartsWidget.setHeight(height);
    return false;
}

function unselectAllECharts() {
    let echartsWidget = PF("demoECharts");
    let echartsInstance = echartsWidget.getEChartsInstance();
    echartsInstance.dispatchAction({
        type: "legendAllSelect"
    });
    echartsInstance.dispatchAction({
        type: "legendInverseSelect"
    });
    return false;
}

function selectAllECharts() {
    let echartsWidget = PF("demoECharts");
    let echartsInstance = echartsWidget.getEChartsInstance();
    echartsInstance.dispatchAction({
        type: "legendAllSelect"
    });
    return false;
}
