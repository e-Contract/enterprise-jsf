/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.output.carousel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

@FacesRenderer(componentFamily = CarouselComponent.COMPONENT_FAMILY, rendererType = CarouselRenderer.RENDERER_TYPE)
public class CarouselRenderer extends CoreRenderer {

    public static final String RENDERER_TYPE = "ejsf.carouselRenderer";

    @Override
    public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
        CarouselComponent carouselComponent = (CarouselComponent) component;
        List<CarouselImage> carouselImages = (List<CarouselImage>) carouselComponent.getValue();
        String clientId = component.getClientId();
        ResponseWriter responseWriter = facesContext.getResponseWriter();
        responseWriter.startElement("div", component);
        responseWriter.writeAttribute("id", clientId, "id");
        responseWriter.writeAttribute("class", "ejsf-carousel", null);
        boolean lazyFirst = carouselComponent.isLazyFirst();
        String width = carouselComponent.getWidth();
        int height = carouselComponent.getHeight();
        String style = "height: " + height + "px;";
        if (null != width) {
            style += "width: " + width + ";";
        }
        responseWriter.writeAttribute("style", style, null);

        {
            responseWriter.startElement("div", null);
            responseWriter.writeAttribute("class", "ejsf-carousel-image-container", null);
            {
                responseWriter.startElement("img", null);
                responseWriter.writeAttribute("class", "ejsf-carousel-image", null);
                if (!lazyFirst) {
                    if (null != carouselImages && !carouselImages.isEmpty()) {
                        CarouselImage carouselImage = carouselImages.get(0);
                        responseWriter.writeAttribute("src", carouselImage.getImage(), null);
                        String caption = carouselImage.getCaption();
                        if (!UIInput.isEmpty(caption)) {
                            responseWriter.writeAttribute("alt", caption, null);
                        }
                    }
                }
                responseWriter.writeAttribute("height", height - 80, null);
                responseWriter.endElement("img");
            }
            {
                responseWriter.startElement("div", null);
                responseWriter.writeAttribute("class", "ejsf-carousel-image-caption", null);
                {
                    responseWriter.startElement("span", null);
                    responseWriter.writeAttribute("class", "ejsf-carousel-image-caption-title", null);
                    responseWriter.endElement("span");
                }
                {
                    responseWriter.startElement("div", null);
                    responseWriter.writeAttribute("class", "ejsf-carousel-image-caption-content", null);
                    responseWriter.endElement("div");
                }
                responseWriter.endElement("div");
            }
            responseWriter.endElement("div");
        }
        {
            responseWriter.startElement("div", null);
            responseWriter.writeAttribute("class", "ejsf-carousel-thumbnails", null);
            {
                responseWriter.startElement("button", null);
                responseWriter.writeAttribute("class", "ejsf-carousel-thumbnails-prev ejsf-carousel-thumbnails-nav-disabled", null);
                responseWriter.write("&lt;");
                responseWriter.endElement("button");
            }
            {
                responseWriter.startElement("div", null);
                responseWriter.writeAttribute("class", "ejsf-carousel-thumbnails-window", null);
                {
                    responseWriter.startElement("div", null);
                    responseWriter.writeAttribute("class", "ejsf-carousel-thumbnails-content", null);
                    responseWriter.endElement("div");
                }
                responseWriter.endElement("div");
            }
            {
                responseWriter.startElement("button", null);
                responseWriter.writeAttribute("class", "ejsf-carousel-thumbnails-next ejsf-carousel-thumbnails-nav-enabled", null);
                responseWriter.write("&gt;");
                responseWriter.endElement("button");
            }
            responseWriter.endElement("div");
        }
        {
            responseWriter.startElement("dialog", null);
            responseWriter.writeAttribute("class", "ejsf-carousel-zoom-dialog", null);
            {
                responseWriter.startElement("img", null);
                responseWriter.writeAttribute("class", "ejsf-carousel-zoom-dialog-image", null);
                responseWriter.endElement("img");
            }
            responseWriter.endElement("dialog");
        }
        responseWriter.endElement("div");

        WidgetBuilder widgetBuilder = getWidgetBuilder(facesContext);
        widgetBuilder.init("EJSFCarousel", carouselComponent);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        widgetBuilder.attr("value", gson.toJson(carouselImages));
        Integer autoPlay = carouselComponent.getAutoPlay();
        if (null != autoPlay) {
            widgetBuilder.attr("autoPlayDelay", autoPlay);
        }
        widgetBuilder.attr("lazyFirst", lazyFirst);
        widgetBuilder.finish();
    }
}
