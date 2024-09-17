var ejsf = ejsf || {};
var EJSF;

(function () {
    let widgetVars = new Map();
    let widgetTypes = new Map();
    let widgetInstances = new Map();
    let removedWidgetIds = new Set();

    class Widget {
        constructor(element) {
            this._element = element;
        }

        init() {
        }

        update(element) {
            this._element = element;
        }

        destroy() {
        }

        get element() {
            return this._element;
        }
    }
    ejsf.Widget = Widget;

    ejsf.registerWidgetType = function (widgetClass) {
        widgetTypes.set(widgetClass.name, widgetClass);
    };

    window.EJSF = function (widgetVar) {
        return widgetVars.get(widgetVar);
    };

    function updateWidgets() {
        let componentElements = document.querySelectorAll("[data-ejsf-widget-update]");
        componentElements.forEach((componentElement) => {
            let componentId = componentElement.getAttribute("id");
            let existingWidgetInstance = widgetInstances.get(componentId);
            if (existingWidgetInstance) {
                existingWidgetInstance.update(componentElement);
                removedWidgetIds.delete(componentId);
            } else {
                let widgetType = componentElement.getAttribute("data-ejsf-widget-type");
                let widgetClass = widgetTypes.get(widgetType);
                let widgetInstance = new widgetClass(componentElement);
                widgetInstances.set(componentId, widgetInstance);
                let widgetVar = componentElement.getAttribute("data-ejsf-widget-var");
                if (widgetVar) {
                    widgetVars.set(widgetVar, widgetInstance);
                }
                widgetInstance.init();
            }
            componentElement.removeAttribute("data-ejsf-widget-update");
        });
        removedWidgetIds.forEach((removedWidgetId) => {
            let removedWidget = widgetInstances.get(removedWidgetId);
            removedWidget.destroy();
            widgetInstances.delete(removedWidgetId);
        });
        removedWidgetIds.clear();
    }

    jsf.ajax.addOnEvent(function (data) {
        if (data.status === "success") {
            queueMicrotask(() => {
                updateWidgets();
            });
        }
    });

    window.addEventListener("load", function () {
        updateWidgets();
        let mutationObserver = new MutationObserver(function (mutationList) {
            for (const mutation of mutationList) {
                for (const removedNode of mutation.removedNodes) {
                    if (removedNode.nodeType !== Node.ELEMENT_NODE) {
                        continue;
                    }
                    let removedWidgetNodeList = removedNode.querySelectorAll("[data-ejsf-widget-type]");
                    removedWidgetNodeList.forEach((removedWidgetElement) => {
                        let removedWidgetId = removedWidgetElement.getAttribute("id");
                        removedWidgetIds.add(removedWidgetId);
                    });
                }
            }
        });
        mutationObserver.observe(document, {
            childList: true,
            subtree: true
        });
    });
})();
