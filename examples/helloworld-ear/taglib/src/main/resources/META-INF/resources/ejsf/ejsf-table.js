(function () {
    class TableWidget extends ejsf.Widget {
        constructor(element, widgetVar) {
            super(element, widgetVar);
        }

        init() {
            this.initListeners();
        }

        update(element) {
            super.update(element);
            this.initListeners();
        }

        initListeners() {
            let $this = this;
            let nextButton = this.element.querySelector(".ejsf-table-button-next");
            nextButton.addEventListener("click", () => {
                $this.nav("next");
            });
            let prevButton = this.element.querySelector(".ejsf-table-button-prev");
            prevButton.addEventListener("click", () => {
                $this.nav("prev");
            });
        }

        nav(action) {
            let options = {
                "javax.faces.behavior.event": action,
                render: this.id
            };
            jsf.ajax.request(this.element, null, options);
        }
    }
    ejsf.registerWidgetType(TableWidget);
})();