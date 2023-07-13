/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.output.echarts;

import java.io.IOException;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.primefaces.PrimeFaces;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

@FacesRenderer(componentFamily = EChartsComponent.COMPONENT_FAMILY,
        rendererType = EChartsRenderer.RENDERER_TYPE)
public class EChartsRenderer extends CoreRenderer {

    public static final String RENDERER_TYPE = "ejsf.echartsRenderer";

    @Override
    public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
        EChartsComponent eChartsComponent = (EChartsComponent) component;
        ResponseWriter responseWriter = facesContext.getResponseWriter();
        String clientId = eChartsComponent.getClientId(facesContext);

        responseWriter.startElement("div", eChartsComponent);
        responseWriter.writeAttribute("id", clientId, "id");
        String style = eChartsComponent.getStyle();
        String styleClass = eChartsComponent.getStyleClass();
        if (null == style && null == styleClass) {
            Integer width = eChartsComponent.getWidth();
            if (null == width) {
                width = 600;
            }
            Integer height = eChartsComponent.getHeight();
            if (null == height) {
                height = 400;
            }
            style = "width: " + width + "px; height: " + height + "px;";
        }
        if (null != style) {
            responseWriter.writeAttribute("style", style, "style");
        }
        if (null != styleClass) {
            responseWriter.writeAttribute("class", styleClass, "styleClass");
        }
        responseWriter.endElement("div");

        WidgetBuilder widgetBuilder = getWidgetBuilder(facesContext);
        widgetBuilder.init("EJSFECharts", eChartsComponent);
        encodeClientBehaviors(facesContext, eChartsComponent);
        widgetBuilder.finish();
    }

    @Override
    public void decode(FacesContext context, UIComponent component) {
        ExternalContext externalContext = context.getExternalContext();
        Map<String, String> requestParameterMap = externalContext.getRequestParameterMap();
        String clientId = component.getClientId(context);
        if (requestParameterMap.containsKey(clientId + "_request_option")) {
            EChartsComponent eChartsComponent = (EChartsComponent) component;
            String value = (String) eChartsComponent.getValue();
            PrimeFaces.current().ajax().addCallbackParam("option", value);
            context.renderResponse();
            return;
        }
        decodeBehaviors(context, component);
    }
}
