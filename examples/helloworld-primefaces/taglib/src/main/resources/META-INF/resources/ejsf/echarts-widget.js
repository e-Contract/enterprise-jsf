PrimeFaces.widget.EChartsWidget = PrimeFaces.widget.BaseWidget.extend({
    init: function (cfg) {
        this._super(cfg);
        let myChart = echarts.init(document.getElementById(this.id));
        let options = {
            source: this.id,
            process: this.id,
            async: true,
            global: false,
            params: [
                {
                    name: this.id + '_request_option',
                    value: true
                }
            ],
            oncomplete: function (xhr, status, args, data) {
                let option = JSON.parse(args.option);
                myChart.setOption(option);
            }
        };
        PrimeFaces.ajax.Request.handle(options);
    }
});
