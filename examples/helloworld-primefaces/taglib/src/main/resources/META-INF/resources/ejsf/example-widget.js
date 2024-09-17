PrimeFaces.widget.ExampleWidget = PrimeFaces.widget.BaseWidget.extend({
    init: function (cfg) {
        this._super(cfg);
        console.log("init");
        if (typeof this.cfg.initialValue !== "undefined") {
            this.value = this.cfg.initialValue;
        } else {
            this.value = null;
        }
    },

    destroy: function () {
        this._super();
        console.log("destroy");
    },

    refresh: function (cfg) {
        this._super(cfg);
        console.log("refresh");
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
