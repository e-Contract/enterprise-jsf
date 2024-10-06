/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import be.e_contract.ejsf.component.output.carousel.CarouselImage;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;

@Named
public class CarouselController {

    private List<CarouselImage> carouselImages;

    @PostConstruct
    public void postConstruct() {
        ImageServlet.setDelay(500);
        ImageServlet.setVariableDimensions(true);
        this.carouselImages = new LinkedList<>();
        for (int idx = 0; idx < 20; idx++) {
            CarouselImage carouselImage = new CarouselImage("./demo/image/" + idx, "./demo/image/thumbnail/" + idx);
            carouselImage.withZoomImage("./demo/image/hd/" + idx);
            if (idx % 2 == 0) {
                carouselImage.withCaptionTitle("Title " + idx);
                carouselImage.withCaption("A description for image " + idx);
            }
            this.carouselImages.add(carouselImage);
        }
    }

    public List<CarouselImage> getCarouselImages() {
        return this.carouselImages;
    }
}
