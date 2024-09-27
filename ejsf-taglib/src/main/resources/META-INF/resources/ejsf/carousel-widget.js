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
        console.log(imageData);
        let carousel = new EJSFCarousel(this.jq.get(0), imageData);
        carousel.init();
        let autoPlayDelay = this.cfg.autoPlayDelay;
        if (autoPlayDelay) {
            carousel.autoPlay(autoPlayDelay);
        }
    }
});
