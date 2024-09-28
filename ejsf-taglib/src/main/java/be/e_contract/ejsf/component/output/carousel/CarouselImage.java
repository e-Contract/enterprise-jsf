/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.output.carousel;

import java.io.Serializable;

public class CarouselImage implements Serializable {

    private final String image;

    private final String thumbnail;

    private String caption;

    private String zoomImage;

    public CarouselImage(String image, String thumbnail) {
        this.image = image;
        this.thumbnail = thumbnail;
    }

    public CarouselImage withCaption(String caption) {
        this.caption = caption;
        return this;
    }

    public CarouselImage withZoomImage(String zoomImage) {
        this.zoomImage = zoomImage;
        return this;
    }

    public String getImage() {
        return this.image;
    }

    public String getCaption() {
        return this.caption;
    }

    public String getThumbnail() {
        return this.thumbnail;
    }

    public String getZoomImage() {
        return this.zoomImage;
    }
}
