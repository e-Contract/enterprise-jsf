/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFCopyButton = PrimeFaces.widget.BaseWidget.extend({

    init: function (cfg) {
        this._super(cfg);
        let $this = this;
        $(this.jqId).on("click", function () {
            $this.copyToClipboard();
        });
    },

    copyToClipboard: function () {
        navigator.clipboard.writeText(this.cfg.value);
        let icon = $(this.jqId + "\\:icon");
        icon.removeClass("pi-copy").addClass(this.cfg.copiedIcon).css("color", "green");
        let $this = this;
        setTimeout(function () {
            $this.restoreIcon();
        }, 3000);
    },

    restoreIcon: function () {
        let icon = $(this.jqId + "\\:icon");
        icon.removeClass(this.cfg.copiedIcon).addClass("pi-copy").css("color", "");
    }
});
