var ejsf = ejsf || {};
var EJSF;

(function () {
    let widgetVars = new Map();
    let widgetTypes = new Map();

    class Widget {
        constructor(element) {
            this._element = element;
            let widgetVar = element.getAttribute("data-ejsf-widgetVar");
            if (null !== widgetVar) {
                widgetVars.set(widgetVar, this);
            }
        }

        get element() {
            return this._element;
        }
    }
    ejsf.Widget = Widget;

    ejsf.registerWidgetType = function (widgetType, widgetClass) {
        widgetTypes.set(widgetType, widgetClass);
    };

    window.EJSF = function (widgetVar) {
        return widgetVars.get(widgetVar);
    };

    window.addEventListener("load", function () {
        let componentElements = document.querySelectorAll("[data-ejsf-type]");
        componentElements.forEach((componentElement) => {
            let widgetType = componentElement.getAttribute("data-ejsf-type");
            let widgetClass = widgetTypes.get(widgetType);
            new widgetClass(componentElement);
        });
    });
})();
