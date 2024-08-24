/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.dialog;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

@FacesRenderer(componentFamily = DialogComponent.COMPONENT_FAMILY,
        rendererType = DialogRenderer.RENDERER_TYPE)
public class DialogRenderer extends CoreRenderer {

    public static final String RENDERER_TYPE = "ejsf.dialogRenderer";

    @Override
    public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
        DialogComponent dialogComponent = (DialogComponent) component;
        ResponseWriter responseWriter = facesContext.getResponseWriter();
        String clientId = dialogComponent.getClientId(facesContext);

        responseWriter.startElement("dialog", dialogComponent);
        responseWriter.writeAttribute("id", clientId, "id");
    }

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        ResponseWriter responseWriter = facesContext.getResponseWriter();
        responseWriter.endElement("dialog");

        DialogComponent dialogComponent = (DialogComponent) component;
        WidgetBuilder widgetBuilder = getWidgetBuilder(facesContext);
        widgetBuilder.init("EJSFDialog", dialogComponent);
        widgetBuilder.finish();
    }
}
