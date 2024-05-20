/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

$(document).ready(function () {
    let d3Widget = PF("d3Widget");
    let width = d3Widget.getWidth();
    let height = d3Widget.getHeight();
    let margin = 30;

    let svg = d3Widget.getD3()
            .append("svg")
            .attr("width", width)
            .attr("height", height);

    let x = d3.scaleLinear()
            .domain([0, 100])
            .range([0, width - 2 * margin]);
    svg.append("g")
            .attr("transform", "translate(" + margin + "," + (height - margin) + ")")
            .call(d3.axisBottom(x));

    let y = d3.scaleLinear()
            .domain([0, 500])
            .range([height - margin, margin]);
    svg.append("g")
            .attr("transform", "translate(" + margin + "," + 0 + ")")
            .call(d3.axisLeft(y));

    let line = d3.line()
            .x(d => x(d.x) + margin)
            .y(d => y(d.y))
            .curve(d3.curveCatmullRom.alpha(.5));

    svg.append("path")
            .datum([
                {x: 0, y: 20},
                {x: 20, y: 20},
                {x: 40, y: 400},
                {x: 60, y: 30},
                {x: 80, y: 200},
                {x: 100, y: 30}
            ])
            .attr("fill", "none")
            .attr("stroke", "steelblue")
            .attr("stroke-width", 1.5)
            .attr("d", line);
});
