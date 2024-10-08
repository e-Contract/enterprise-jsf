(function () {
    class ExampleWidget extends ejsf.Widget {
        constructor(element, widgetVar) {
            super(element, widgetVar);
        }

        setValue(value) {
            super.element.innerText = value;
        }

        init() {
            console.log("init");
        }

        update(element) {
            super.update(element);
            console.log("update");
        }

        destroy() {
            console.log("destroy");
        }
    }
    ejsf.registerWidgetType(ExampleWidget);
})();
