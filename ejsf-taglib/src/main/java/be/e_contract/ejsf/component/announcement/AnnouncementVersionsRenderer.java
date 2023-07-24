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

@FacesRenderer(componentFamily = AnnouncementVersionsComponent.COMPONENT_FAMILY,
        rendererType = AnnouncementVersionsRenderer.RENDERER_TYPE)
public class AnnouncementVersionsRenderer extends Renderer {

    public static final String RENDERER_TYPE = "ejsf.announcementVersionsRenderer";

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        AnnouncementVersionsComponent dataList = (AnnouncementVersionsComponent) component;
        ResponseWriter responseWriter = context.getResponseWriter();
        responseWriter.startElement("div", component);
        String clientId = dataList.getClientId(context);
        responseWriter.writeAttribute("id", clientId, "id");
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        AnnouncementVersionsComponent announcementVersionsComponent = (AnnouncementVersionsComponent) component;
        for (int idx = 0; idx < announcementVersionsComponent.getRowCount(); idx++) {
            announcementVersionsComponent.setRowIndex(idx);
            for (UIComponent child : component.getChildren()) {
                child.encodeAll(context);
            }
        }
        announcementVersionsComponent.setRowIndex(-1);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter responseWriter = context.getResponseWriter();
        responseWriter.endElement("div");
    }
}
