/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFDialog = PrimeFaces.widget.BaseWidget.extend({

    init: function (cfg) {
        this._super(cfg);
        let dialogElement = document.getElementById(this.id);
        dialogElement.addEventListener("click", event => {
            const dialogDimensions = dialogElement.getBoundingClientRect();
            if (event.clientX < dialogDimensions.left ||
                    event.clientX > dialogDimensions.right ||
                    event.clientY < dialogDimensions.top ||
                    event.clientY > dialogDimensions.bottom) {
                dialogElement.close();
            }
        });
    },

    show: function () {
        let dialogElement = document.getElementById(this.id);
        dialogElement.showModal();
    },

    hide: function () {
        let dialogElement = document.getElementById(this.id);
        dialogElement.close();
    }
});
