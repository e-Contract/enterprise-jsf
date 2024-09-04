var ejsf = ejsf || {};
var EJSF;

(function () {
    let widgetVars = new Map();
    let widgetTypes = new Map();
    let widgetInstances = new Map();

    class Widget {
        constructor(element) {
            this._element = element;
            let widgetVar = element.getAttribute("data-ejsf-widget-var");
            if (null !== widgetVar) {
                widgetVars.set(widgetVar, this);
            }
        }

        init() {
        }

        update(oldWidget) {
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
            let widgetType = componentElement.getAttribute("data-ejsf-widget-type");
            let widgetClass = widgetTypes.get(widgetType);
            let componentId = componentElement.getAttribute("id");
            let oldWidgetInstance = widgetInstances.get(componentId);
            let widgetInstance = new widgetClass(componentElement);
            if (oldWidgetInstance) {
                widgetInstance.update(oldWidgetInstance);
                oldWidgetInstance.destroy();
            } else {
                widgetInstance.init();
            }
            widgetInstances.set(componentId, widgetInstance);
            componentElement.removeAttribute("data-ejsf-widget-update");
        });
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
    });
})();
