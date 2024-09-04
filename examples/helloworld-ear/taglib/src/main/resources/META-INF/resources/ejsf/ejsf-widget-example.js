(function () {
    class ExampleWidget extends ejsf.Widget {
        constructor(element) {
            super(element);
        }

        setValue(value) {
            super.element.innerText = value;
        }

        init() {
            console.log("init");
        }

        update(oldWidget) {
            console.log("update");
        }

        destroy() {
            console.log("destroy");
        }
    }
    ejsf.registerWidgetType(ExampleWidget);
})();
