/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

window.addEventListener("load", () => {
    let katexElements = $("[data-ejsf-katex]");
    katexElements.each(function () {
        let katexElement = $(this);
        let katexText = katexElement.text();
        if (katexText.length === 0) {
            renderMathInElement(document.body, {
                delimiters: [
                    {left: '$$', right: '$$', display: true},
                    {left: '$', right: '$', display: false},
                    {left: '\\[', right: '\\]', display: true},
                    {left: '\\(', right: '\\)', display: false},
                    {left: '\\begin{equation}', right: '\\end{equation}', display: true},
                    {left: '\\begin{align}', right: '\\end{align}', display: true}
                ]
            });
        } else {
            katex.render(katexText, katexElement.get(0), {
                throwOnError: false
            });
        }
    });
});
