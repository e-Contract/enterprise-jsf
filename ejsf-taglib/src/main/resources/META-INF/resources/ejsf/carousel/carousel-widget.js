/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

PrimeFaces.widget.EJSFCarousel = PrimeFaces.widget.BaseWidget.extend({

    init: function (cfg) {
        this._super(cfg);
        let imageData = JSON.parse(this.cfg.value);
        this.carousel = new EJSFCarousel(this.jq.get(0), imageData);
        this.carousel.init();
        let autoPlayDelay = this.cfg.autoPlayDelay;
        if (autoPlayDelay) {
            this.carousel.autoPlay(autoPlayDelay);
        }
    },

    destroy: function () {
        this.carousel.destroy();
        this._super();
    }
});
