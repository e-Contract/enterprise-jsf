(function () {
    class LazyTableWidget extends ejsf.Widget {
        constructor(element, widgetVar) {
            super(element, widgetVar);
        }

        init() {
            this.initListeners();
            let $this = this;
            setTimeout(function () {
                $this.lazyLoad();
            }, 0);
        }

        update(element) {
            super.update(element);
            this.initListeners();
        }

        lazyLoad() {
            this.action("load");
        }

        initListeners() {
            let $this = this;
            let nextButton = this.element.querySelector(".ejsf-table-button-next");
            nextButton.addEventListener("click", () => {
                $this.action("next");
            });
            let prevButton = this.element.querySelector(".ejsf-table-button-prev");
            prevButton.addEventListener("click", () => {
                $this.action("prev");
            });
        }

        action(action) {
            let options = {
                "javax.faces.behavior.event": action,
                render: this.id
            };
            jsf.ajax.request(this.element, null, options);
        }
    }
    ejsf.registerWidgetType(LazyTableWidget);
})();