(function () {
    class UpdateWidget extends ejsf.Widget {
        constructor(element, widgetVar) {
            super(element, widgetVar);
        }

        updateData(data) {

        }
    }
    ejsf.UpdateWidget = UpdateWidget;

    window.addEventListener("load", () => {
        jsf.ajax.addOnEvent(function (data) {
            if (data.status === "success") {
                queueMicrotask(() => {
                    let widgetUpdates = data.responseXML
                            .querySelector("extension#widget-updates")
                            .children;
                    for (let idx = 0; idx < widgetUpdates.length; idx++) {
                        let widgetUpdate = widgetUpdates[idx];
                        let widgetClientId = widgetUpdate.getAttribute("widget");
                        let updateData = JSON.parse(widgetUpdate.textContent);
                        let widget = ejsf.getWidget(widgetClientId);
                        widget.updateData(updateData);
                    }
                });
            }
        });
    });
})();
