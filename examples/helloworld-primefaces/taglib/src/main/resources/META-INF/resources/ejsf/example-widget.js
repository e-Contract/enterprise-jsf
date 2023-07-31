PrimeFaces.widget.ExampleWidget = PrimeFaces.widget.BaseWidget.extend({
    init: function (cfg) {
        this._super(cfg);
        if (typeof this.cfg.initialValue !== "undefined") {
            this.value = this.cfg.initialValue;
        } else {
            this.value = null;
        }
    },

    setValue: function (value) {
        this.value = value;
        if (null !== value) {
            this.jq.text(value);
        } else {
            this.jq.text("");
        }
    },

    getValue: function () {
        return this.value;
    }
});
