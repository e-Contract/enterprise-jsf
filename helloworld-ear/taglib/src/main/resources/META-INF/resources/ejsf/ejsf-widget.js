var ejsf = ejsf || {};
var EJSF;

(function () {
    let widgetVars = new Map();

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

    window.EJSF = function (widgetVar) {
        return widgetVars.get(widgetVar);
    };
})();
