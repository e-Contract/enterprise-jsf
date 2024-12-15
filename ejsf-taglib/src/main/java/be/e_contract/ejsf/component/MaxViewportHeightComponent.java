/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component;

import java.io.IOException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent(MaxViewportHeightComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "ejsf", name = "max-viewport-height.js")
})
public class MaxViewportHeightComponent extends UIComponentBase {

    public static final String COMPONENT_TYPE = "ejsf.maxViewportHeight";

    public static final String COMPONENT_FAMILY = "ejsf";

    public MaxViewportHeightComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        footer,
    }

    public String getFooter() {
        return (String) getStateHelper().get(PropertyKeys.footer);
    }

    public void setFooter(String footer) {
        getStateHelper().put(PropertyKeys.footer, footer);
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        if (!isRendered()) {
            return;
        }
        ResponseWriter responseWriter = context.getResponseWriter();
        responseWriter.startElement("span", this);
        String clientId = getClientId(context);
        responseWriter.writeAttribute("id", clientId, "id");
        UIComponent parentComponent = getParent();
        String parentClientId = parentComponent.getClientId();
        responseWriter.writeAttribute("data-ejsf-max-viewport-height", parentClientId, null);

        String footer = getFooter();
        if (null != footer) {
            UIComponent footerComponent = findComponent(footer);
            String footerId;
            if (null != footerComponent) {
                footerId = footerComponent.getClientId();
            } else {
                footerId = footer;
            }
            responseWriter.writeAttribute("data-ejsf-max-viewport-height-footer", footerId, "footer");
        }

        responseWriter.endElement("span");
    }
}
