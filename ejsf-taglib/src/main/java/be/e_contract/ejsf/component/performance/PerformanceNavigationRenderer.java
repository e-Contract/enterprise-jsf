/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.performance;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.FacesRenderer;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

@FacesRenderer(componentFamily = PerformanceNavigationComponent.COMPONENT_FAMILY, rendererType = PerformanceNavigationRenderer.RENDERER_TYPE)
public class PerformanceNavigationRenderer extends CoreRenderer {

    public static final String RENDERER_TYPE = "ejsf.performanceNavigationRenderer";

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        PerformanceNavigationComponent performanceNavigationComponent = (PerformanceNavigationComponent) component;

        WidgetBuilder widgetBuilder = getWidgetBuilder(context);
        widgetBuilder.init("EJSFPerformanceNavigation", performanceNavigationComponent);
        encodeClientBehaviors(context, performanceNavigationComponent);
        widgetBuilder.finish();
    }

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }
}
