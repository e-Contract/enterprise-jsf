/*
 * Enterprise JSF project.
 *
 * Copyright 2024-2026 e-Contract.be BV. All rights reserved.
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
        this._renderInProgress = false;
        this._pending = null;
        this._scheduleRender(this.cfg.value);
    },

    /**
     * Sets the value.
     *
     * @param {string} value
     */
    setValue: function (value, callback) {
        this._scheduleRender(value, callback);
    },

    _render: function (value) {
        let html = DOMPurify.sanitize(marked.parse(value));
        $(this.jqId).html(html);
    },

    _scheduleRender: function (value, callback) {
        if (this._renderInProgress) {
            this._pending = {
                value: value,
                callback: callback
            };
            return;
        }
        this._renderInProgress = true;
        const _this = this;
        requestAnimationFrame(function () {
            _this._render(value);
            if (callback) {
                callback();
            }
            _this._renderInProgress = false;
            if (_this._pending !== null) {
                const next = _this._pending;
                _this._pending = null;
                _this._scheduleRender(next.value, next.callback);
            }
        });
    }
});
