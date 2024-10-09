(function () {
    class TableJavascriptUpdateWidget extends ejsf.Widget {
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
                "javax.faces.behavior.event": action
            };
            jsf.ajax.request(this.element, null, options);
        }

        update(data) {
            for (let idx = 0; idx < data.length; idx++) {
                let td = this.element.querySelector("[data-ejsf-table-page-row=\"" + idx + "\"]");
                td.innerText = data[idx];
            }
        }

        static update(id, data) {
            let widget = ejsf.getWidget(id);
            widget.update(data);
        }
    }
    ejsf.TableJavascriptUpdateWidget = TableJavascriptUpdateWidget;
    ejsf.registerWidgetType(TableJavascriptUpdateWidget);
})();