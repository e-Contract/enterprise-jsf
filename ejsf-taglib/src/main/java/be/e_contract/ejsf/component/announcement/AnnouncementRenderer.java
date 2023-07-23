/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.announcement;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;

@FacesRenderer(componentFamily = AnnouncementComponent.COMPONENT_FAMILY, rendererType = AnnouncementRenderer.RENDERER_TYPE)
public class AnnouncementRenderer extends Renderer {

    public static final String RENDERER_TYPE = "ejsf.announcementRenderer";

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter responseWriter = context.getResponseWriter();
        String clientId = component.getClientId(context);
        responseWriter.startElement("div", component);
        responseWriter.writeAttribute("id", clientId, "id");
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter responseWriter = context.getResponseWriter();
        responseWriter.endElement("div");
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        AnnouncementComponent announcementComponent = (AnnouncementComponent) component;
        boolean accepted = announcementComponent.hasAnnouncementAccepted();
        if (accepted) {
            return;
        }
        for (UIComponent child : component.getChildren()) {
            child.encodeAll(context);
        }
    }
}
