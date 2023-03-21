/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

if (PrimeFaces.widget.Dialog) {
    PrimeFaces.widget.Dialog = PrimeFaces.widget.Dialog.extend({
        show: function () {
            this._super();
            PrimeFaces.getWidgetsByType(PrimeFaces.widget.Tooltip).forEach(
                    function (tooltip) {
                        tooltip.hide();
                    }
            );
        },

        hide: function () {
            this._super();
            PrimeFaces.getWidgetsByType(PrimeFaces.widget.Tooltip).forEach(
                    function (tooltip) {
                        tooltip.hide();
                    }
            );
        }
    });
}

if (PrimeFaces.widget.ConfirmDialog) {
    PrimeFaces.widget.ConfirmDialog = PrimeFaces.widget.ConfirmDialog.extend({
        show: function () {
            this._super();
            PrimeFaces.getWidgetsByType(PrimeFaces.widget.Tooltip).forEach(
                    function (tooltip) {
                        tooltip.hide();
                    }
            );
        },

        hide: function () {
            this._super();
            PrimeFaces.getWidgetsByType(PrimeFaces.widget.Tooltip).forEach(
                    function (tooltip) {
                        tooltip.hide();
                    }
            );
        }
    });
}
