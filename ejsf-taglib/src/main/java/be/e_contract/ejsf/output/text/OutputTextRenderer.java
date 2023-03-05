/*
 * Enterprise JSF project.
 *
 * Copyright 2022-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.output.text;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

@FacesRenderer(componentFamily = OutputTextComponent.COMPONENT_FAMILY, rendererType = OutputTextRenderer.RENDERER_TYPE)
public class OutputTextRenderer extends CoreRenderer {

    public static final String RENDERER_TYPE = "ejsf.outputTextRenderer";

    @Override
    public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
        OutputTextComponent outputTextComponent = (OutputTextComponent) component;
        Object objValue = outputTextComponent.getValue();
        String value;
        if (null != objValue) {
            value = objValue.toString();
        } else {
            value = null;
        }

        String clientId = component.getClientId();
        ResponseWriter responseWriter = facesContext.getResponseWriter();

        responseWriter.startElement("span", component);
        responseWriter.writeAttribute("id", clientId, "id");
        String style = outputTextComponent.getStyle();
        if (null != style) {
            responseWriter.writeAttribute("style", style, "style");
        }
        String styleClass = outputTextComponent.getStyleClass();
        if (null != styleClass) {
            responseWriter.writeAttribute("class", styleClass, "styleClass");
        }
        String unit = outputTextComponent.getUnit();
        if (null != value) {
            responseWriter.writeText(value, "value");
            if (null != unit) {
                responseWriter.writeText(" " + unit, "unit");
            }
        }
        responseWriter.endElement("span");

        WidgetBuilder widgetBuilder = getWidgetBuilder(facesContext);
        widgetBuilder.init("EJSFOutputText", outputTextComponent);
        widgetBuilder.attr("initialValue", value);
        widgetBuilder.attr("unit", unit);
        widgetBuilder.finish();
    }
}
