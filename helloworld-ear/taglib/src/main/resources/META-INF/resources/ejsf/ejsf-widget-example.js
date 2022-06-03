(function () {
    class ExampleWidget extends ejsf.Widget {
        constructor(element) {
            super(element);
        }

        setValue(value) {
            super.element.innerText = value;
        }
    }
    ejsf.ExampleWidget = ExampleWidget;
    ejsf.registerWidgetType("ExampleWidget", ejsf.ExampleWidget);
})();
