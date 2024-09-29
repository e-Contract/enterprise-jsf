/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.output.carousel;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;
import org.primefaces.component.api.Widget;

@FacesComponent(CarouselComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "ejsf", name = "carousel/carousel.css"),
    @ResourceDependency(library = "ejsf", name = "carousel/carousel.js"),
    @ResourceDependency(library = "ejsf", name = "carousel/carousel-widget.js")
})
public class CarouselComponent extends UIOutput implements Widget {

    public static final String COMPONENT_TYPE = "ejsf.carouselComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public CarouselComponent() {
        setRendererType(CarouselRenderer.RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        width,
        height,
        autoPlay,
        lazyFirst
    }

    public String getWidth() {
        return (String) getStateHelper().eval(PropertyKeys.width, null);
    }

    public void setWidth(String width) {
        getStateHelper().put(PropertyKeys.width, width);
    }

    public int getHeight() {
        return (int) getStateHelper().eval(PropertyKeys.height, null);
    }

    public void setHeight(int height) {
        getStateHelper().put(PropertyKeys.height, height);
    }

    public Integer getAutoPlay() {
        return (Integer) getStateHelper().eval(PropertyKeys.autoPlay, null);
    }

    public void setAutoPlay(Integer autoPlay) {
        getStateHelper().put(PropertyKeys.autoPlay, autoPlay);
    }

    public boolean isLazyFirst() {
        return (boolean) getStateHelper().eval(PropertyKeys.lazyFirst, false);
    }

    public void setLazyFirst(boolean lazyFirst) {
        getStateHelper().put(PropertyKeys.lazyFirst, lazyFirst);
    }
}
