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
        String width = carouselComponent.getWidth();
        String height = carouselComponent.getHeight();
        if (null != width || null != height) {
            String style = "";
            if (null != width) {
                style += "width: " + width + ";";
            }
            if (null != height) {
                style += "height: " + height + ";";
            }
            responseWriter.writeAttribute("style", style, null);
        }
        {
            responseWriter.startElement("div", null);
            responseWriter.writeAttribute("class", "ejsf-carousel-image-container", null);
            {
                responseWriter.startElement("img", null);
                responseWriter.writeAttribute("class", "ejsf-carousel-image", null);
                responseWriter.writeAttribute("src", carouselImages.get(0).getImage(), null);
                responseWriter.endElement("img");
            }
            {
                responseWriter.startElement("div", null);
                responseWriter.writeAttribute("class", "ejsf-carousel-image-caption", null);
                responseWriter.endElement("div");
            }
            responseWriter.endElement("div");
        }
        {
            responseWriter.startElement("div", null);
            responseWriter.writeAttribute("class", "ejsf-carousel-thumbnails", null);
            {
                responseWriter.startElement("div", null);
                responseWriter.writeAttribute("class", "ejsf-carousel-thumbnails-prev", null);
                responseWriter.write("&lt;");
                responseWriter.endElement("div");
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
                responseWriter.startElement("div", null);
                responseWriter.writeAttribute("class", "ejsf-carousel-thumbnails-next", null);
                responseWriter.write("&gt;");
                responseWriter.endElement("div");
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
        widgetBuilder.finish();
    }
}
