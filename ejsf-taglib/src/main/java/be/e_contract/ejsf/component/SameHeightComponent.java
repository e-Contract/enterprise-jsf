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
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent(SameHeightComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "ejsf", name = "same-height.js")
})
public class SameHeightComponent extends UIComponentBase {

    public static final String COMPONENT_TYPE = "ejsf.sameHeightComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    enum PropertyKeys {
        group,
    }

    public SameHeightComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getGroup() {
        return (String) getStateHelper().eval(PropertyKeys.group, "default");
    }

    public void setGroup(String group) {
        getStateHelper().put(PropertyKeys.group, group);
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        ResponseWriter responseWriter = context.getResponseWriter();
        responseWriter.startElement("span", this);
        String clientId = getClientId(context);
        responseWriter.writeAttribute("id", clientId, "id");
        String groupName = getGroup();
        responseWriter.writeAttribute("data-ejsf-same-height", groupName, "group");
        responseWriter.endElement("span");
    }
}
