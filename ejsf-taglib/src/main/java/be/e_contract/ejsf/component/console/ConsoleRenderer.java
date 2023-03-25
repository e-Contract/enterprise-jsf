/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.console;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

@FacesRenderer(componentFamily = ConsoleComponent.COMPONENT_FAMILY, rendererType = ConsoleRenderer.RENDERER_TYPE)
public class ConsoleRenderer extends CoreRenderer {

    public static final String RENDERER_TYPE = "ejsf.consoleRenderer";

    @Override
    public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
        ConsoleComponent consoleComponent = (ConsoleComponent) component;
        String clientId = consoleComponent.getClientId();
        ResponseWriter responseWriter = facesContext.getResponseWriter();

        responseWriter.startElement("div", component);
        responseWriter.writeAttribute("id", clientId, "id");

        String style = consoleComponent.getStyle();
        if (null == style) {
            style = "height: 200px;";
        }
        responseWriter.writeAttribute("style", style, "style");

        String styleClass = consoleComponent.getStyleClass();
        if (null == styleClass) {
            styleClass = "ejsf-console";
        } else {
            styleClass = "ejsf-console " + styleClass;
        }
        responseWriter.writeAttribute("class", styleClass, "styleClass");

        responseWriter.endElement("div");

        WidgetBuilder widgetBuilder = getWidgetBuilder(facesContext);
        widgetBuilder.init("EJSFConsole", consoleComponent);
        boolean timestamp = consoleComponent.isTimestamp();
        widgetBuilder.attr("timestamp", timestamp);
        widgetBuilder.finish();
    }
}
