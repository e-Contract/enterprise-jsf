/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFVisNetwork = PrimeFaces.widget.BaseWidget.extend({
    init: function (cfg) {
        this._super(cfg);
        let $this = this;
        let ajaxRequestOptions = {
            source: this.id,
            process: this.id,
            async: true,
            global: false,
            params: [
                {
                    name: this.id + "_request_data",
                    value: true
                }
            ],
            oncomplete: function (xhr, status, args, data) {
                if (typeof args.data === "undefined") {
                    return;
                }
                let parsedData = JSON.parse(args.data);
                let visNetworkNodes = new vis.DataSet(parsedData.nodes);
                let visNetworkEdges = new vis.DataSet(parsedData.edges);
                let visNetworkData = {
                    nodes: visNetworkNodes,
                    edges: visNetworkEdges
                };
                let visNetworkOptions;
                if (typeof parsedData.options !== "undefined") {
                    visNetworkOptions = parsedData.options;
                } else {
                    visNetworkOptions = {};
                }
                let visNetworkContainer = document.getElementById($this.id);
                let visNetwork = new vis.Network(visNetworkContainer, visNetworkData, visNetworkOptions);
                visNetwork.on("doubleClick", function (event) {
                    if (typeof event.nodes[0] === "undefined") {
                        return;
                    }
                    let doubleClickBehaviorOptions = {
                        params: [
                            {
                                name: $this.id + "_node",
                                value: event.nodes[0]
                            }
                        ]
                    };
                    $this.callBehavior("doubleClick", doubleClickBehaviorOptions);
                });
            }
        };
        PrimeFaces.ajax.Request.handle(ajaxRequestOptions);
    }
});
