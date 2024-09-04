/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

var ejsf = ejsf || {};
(function () {

    ejsf.drawBattery = function (canvasElement, level) {
        if (level > 100) {
            level = 100;
        }
        if (level < 0) {
            level = 0;
        }
        let canvas = canvasElement.get(0);
        let context = canvas.getContext("2d");
        let height = canvas.height - 5;
        let length = canvas.width - 5;
        let tip = 7;
        let offset = 2;
        context.moveTo(offset, offset);
        context.lineTo(length - tip + offset, 0 + offset);
        context.lineTo(length - tip + offset, (height - tip) / 2 + offset);
        context.lineTo(length + offset, (height - tip) / 2 + offset);
        context.lineTo(length + offset, (height + tip) / 2 + offset);
        context.lineTo(length - tip + offset, (height + tip) / 2 + offset);
        context.lineTo(length - tip + offset, height + offset);
        context.lineTo(offset, height + offset);
        context.lineTo(offset, offset);
        context.lineWidth = 2;
        context.closePath();
        context.strokeStyle = "#a0a0a0";
        context.stroke();

        if (level <= 25) {
            context.fillStyle = "#ff0000";
        } else {
            context.fillStyle = "#00ff00";
        }
        let border = 3;
        let levelX = border + (length - tip - 3 * border) * level / 100;
        context.fillRect(border + offset, border + offset, levelX, height - 2 * border);

        context.fillStyle = "#ffffff";
        context.fillRect(levelX + offset, border + offset, length - levelX - tip - border, height - 2 * border);
    };

    ejsf.updateBatteries = function () {
        let canvasElements = $("[data-ejsf-battery-render]");
        canvasElements.each(function () {
            let canvasElement = $(this);
            let updateCanvas = canvasElement.attr("data-ejsf-update");
            canvasElement.removeAttr("data-ejsf-update");
            let batteryElementId = canvasElement.attr("data-ejsf-battery-render");
            let batteryElement = $("#" + batteryElementId);
            let value = batteryElement.attr("data-ejsf-battery-value");
            let update = batteryElement.attr("data-ejsf-update");
            batteryElement.removeAttr("data-ejsf-update");
            if (updateCanvas) {
                console.log("update render");
                ejsf.drawBattery(canvasElement, value);
            } else if (update) {
                console.log("update data");
                ejsf.drawBattery(canvasElement, value);
            }
        });

    };
})();

$(document).ready(function () {
    queueMicrotask(() => {
        ejsf.updateBatteries();
    });
    if (window.jsf && jsf.ajax) {
        jsf.ajax.addOnEvent(function (data) {
            if (data.status === "success") {
                queueMicrotask(() => {
                    ejsf.updateBatteries();
                });
            }
        });
    }
    $(document).on("pfAjaxComplete", function () {
        queueMicrotask(() => {
            ejsf.updateBatteries();
        });
    });
    let mutationObserver = new MutationObserver(function (mutationList) {
        for (const mutation of mutationList) {
            for (const removedNode of mutation.removedNodes) {
                if (removedNode.nodeType !== Node.ELEMENT_NODE) {
                    continue;
                }
                let batteryRenderNodeList = removedNode.querySelectorAll("[data-ejsf-battery-render]");
                batteryRenderNodeList.forEach((batteryRender) => {
                    console.log("removed: " + batteryRender.attributes.getNamedItem("data-ejsf-battery-render").value);
                });
            }
        }
    });
    mutationObserver.observe(document, {
        childList: true,
        subtree: true
    });
});


