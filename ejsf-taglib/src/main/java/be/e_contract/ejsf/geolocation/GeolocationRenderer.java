/*
 * Enterprise JSF project.
 *
 * Copyright 2022-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.geolocation;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.FacesRenderer;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

@FacesRenderer(componentFamily = GeolocationComponent.COMPONENT_FAMILY, rendererType = GeolocationRenderer.RENDERER_TYPE)
public class GeolocationRenderer extends CoreRenderer {

    public static final String RENDERER_TYPE = "ejsf.geolocationRenderer";

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        GeolocationComponent geolocationComponent = (GeolocationComponent) component;

        WidgetBuilder widgetBuilder = getWidgetBuilder(context);
        widgetBuilder.init("EJSFGeolocation", geolocationComponent);
        widgetBuilder.attr("MAXIMUM_AGE", geolocationComponent.getMaximumAge());
        widgetBuilder.attr("TIMEOUT", geolocationComponent.getTimeout());
        widgetBuilder.attr("HIGH_ACCURACY", geolocationComponent.isEnableHighAccuracy());
        widgetBuilder.attr("AUTO_START", geolocationComponent.isAutoStart());
        encodeClientBehaviors(context, geolocationComponent);
        widgetBuilder.finish();
    }

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }
}
