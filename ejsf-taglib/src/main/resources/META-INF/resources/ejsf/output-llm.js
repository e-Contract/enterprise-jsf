/*
 * Enterprise JSF project.
 *
 * Copyright 2024-2025 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

marked.use(markedKatex({
    globalGroup: true,
    nonStandard: true
}), {
    hooks: {
        preprocess: function (markdown) {
            if (/\\(.*\\)|\\[.*\\]/.test(markdown)) {
                const katexNode = document.createElement("div");
                katexNode.textContent = markdown;
                renderMathInElement(katexNode, {
                    delimiters: [
                        {left: "\\(", right: "\\)", display: false},
                        {left: "\\[", right: "\\]", display: true}
                    ],
                    throwOnError: false
                });
                return katexNode.innerHTML;
            }
            return markdown;
        }
    }
});

marked.use(markedHighlight.markedHighlight({
    emptyLangClass: "hljs",
    langPrefix: "hljs language-",
    highlight: function (code, lang, info) {
        let language = hljs.getLanguage(lang) ? lang : "plaintext";
        return hljs.highlight(code, {language: language}).value;
    }
}));

PrimeFaces.widget.EJSFOutputLLM = PrimeFaces.widget.BaseWidget.extend({

    init: function (cfg) {
        this._super(cfg);
        let html = DOMPurify.sanitize(marked.parse(this.cfg.value));
        $(this.jqId).html(html);
    },

    /**
     * Sets the value.
     *
     * @param {string} value
     */
    setValue: function (value) {
        let html = DOMPurify.sanitize(marked.parse(value));
        $(this.jqId).html(html);
    }
});
