/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.robots;

import java.io.IOException;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent(RobotsHeadComponent.COMPONENT_TYPE)
public class RobotsHeadComponent extends UIComponentBase {

    public static final String COMPONENT_TYPE = "ejsf.robotsHeadComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public RobotsHeadComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        ResponseWriter responseWriter = context.getResponseWriter();
        responseWriter.startElement("meta", this);
        responseWriter.writeAttribute("name", "robots", null);
        responseWriter.writeAttribute("content", "noindex, nofollow", null);
        responseWriter.endElement("meta");
    }
}
