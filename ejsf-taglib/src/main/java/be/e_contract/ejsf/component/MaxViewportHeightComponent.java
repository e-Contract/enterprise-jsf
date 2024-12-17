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
        _for,
        minHeight,
    }

    public String getFooter() {
        return (String) getStateHelper().get(PropertyKeys.footer);
    }

    public void setFooter(String footer) {
        getStateHelper().put(PropertyKeys.footer, footer);
    }

    public String getFor() {
        return (String) getStateHelper().get(PropertyKeys._for);
    }

    public void setFor(String _for) {
        getStateHelper().put(PropertyKeys._for, _for);
    }

    public Integer getMinHeight() {
        return (Integer) getStateHelper().get(PropertyKeys.minHeight);
    }

    public void setMinHeight(Integer minHeight) {
        getStateHelper().put(PropertyKeys.minHeight, minHeight);
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
        String parentClientId;
        String _for = getFor();
        if (null != _for) {
            UIComponent forComponent = findComponent(_for);
            if (null != forComponent) {
                parentClientId = forComponent.getClientId();
            } else {
                parentClientId = _for;
            }
        } else {
            UIComponent parentComponent = getParent();
            parentClientId = parentComponent.getClientId();
        }
        responseWriter.writeAttribute("data-ejsf-max-viewport-height", parentClientId, null);

        Integer minHeight = getMinHeight();
        if (null != minHeight) {
            responseWriter.writeAttribute("data-ejsf-max-viewport-height-min", minHeight, "minHeiht");
        }

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
