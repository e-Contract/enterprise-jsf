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

    window.addEventListener("load", function () {
        let componentElements =
                document.querySelectorAll("[data-ejsf-type='ExampleWidget']");
        componentElements.forEach((componentElement) => {
            new ejsf.ExampleWidget(componentElement);
        });
    });
})();
