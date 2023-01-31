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
}
