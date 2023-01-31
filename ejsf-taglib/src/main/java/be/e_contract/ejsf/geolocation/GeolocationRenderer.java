/*
 * Enterprise JSF project.
 *
 * Copyright 2022-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.geolocation;

import java.io.IOException;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.render.FacesRenderer;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesRenderer(componentFamily = GeolocationComponent.COMPONENT_FAMILY, rendererType = GeolocationRenderer.RENDERER_TYPE)
public class GeolocationRenderer extends CoreRenderer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeolocationRenderer.class);

    public static final String RENDERER_TYPE = "ejsf.geolocationRenderer";

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        GeolocationComponent trackingComponent = (GeolocationComponent) component;

        WidgetBuilder widgetBuilder = getWidgetBuilder(context);
        widgetBuilder.init("EJSFGeolocation", trackingComponent);
        widgetBuilder.finish();
    }

    @Override
    public void decode(FacesContext context, UIComponent component) {
        LOGGER.debug("decode");
        GeolocationComponent geolocationComponent = (GeolocationComponent) component;
        ExternalContext externalContext = context.getExternalContext();
        Map<String, String> requestParameterMap = externalContext.getRequestParameterMap();
        String clientId = component.getClientId(context);
        if (!requestParameterMap.containsKey(clientId)) {
            return;
        }

        String latitudeParam = requestParameterMap.get(clientId + "_latitude");
        if (UIInput.isEmpty(latitudeParam)) {
            LOGGER.warn("missing latitude parameter");
            return;
        }
        double latitude = Double.parseDouble(latitudeParam);

        String longitudeParam = requestParameterMap.get(clientId + "_longitude");
        if (UIInput.isEmpty(longitudeParam)) {
            LOGGER.warn("missing longitude parameter");
            return;
        }
        double longitude = Double.parseDouble(longitudeParam);

        String accuracyParam = requestParameterMap.get(clientId + "_accuracy");
        if (UIInput.isEmpty(accuracyParam)) {
            LOGGER.warn("missing accuracy parameter");
            return;
        }
        double accuracy = Double.parseDouble(accuracyParam);
        LOGGER.debug("queueing event: GeolocationEvent {} {} {}", latitude, longitude, accuracy);
        geolocationComponent.queueEvent(new GeolocationEvent(component, latitude, longitude, accuracy));
    }
}
