/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFECharts = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function (cfg) {
        this._super(cfg);
        this.myChart = echarts.init(document.getElementById(this.id));
        let $this = this;
        let ajaxRequestOptions = {
            source: this.id,
            process: this.id,
            async: true,
            global: false,
            params: [
                {
                    name: this.id + "_request_option",
                    value: true
                }
            ],
            oncomplete: function (xhr, status, args, data) {
                let option = JSON.parse(args.option);
                $this.myChart.setOption(option);
                $this.myChart.on("click", function (params) {
                    let clickBehaviorOptions = {
                        params: [
                            {
                                name: $this.id + "_name",
                                value: params.name
                            },
                            {
                                name: $this.id + "_dataIndex",
                                value: params.dataIndex
                            },
                            {
                                name: $this.id + "_seriesIndex",
                                value: params.seriesIndex
                            }
                        ]
                    };
                    $this.callBehavior("click", clickBehaviorOptions);
                });
            }
        };
        PrimeFaces.ajax.Request.handle(ajaxRequestOptions);
    },

    getEChartsInstance: function () {
        return this.myChart;
    },

    setHeight: function (height) {
        this.jq.height(height);
        this.myChart.resize();
    }
});
