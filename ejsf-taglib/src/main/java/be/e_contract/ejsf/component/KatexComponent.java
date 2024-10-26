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

@FacesComponent(KatexComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "ejsf", name = "katex/katex.min.css"),
    @ResourceDependency(library = "ejsf", name = "katex/katex.min.js"),
    @ResourceDependency(library = "ejsf", name = "katex/auto-render.min.js"),
    @ResourceDependency(library = "ejsf", name = "katex.js")
})
public class KatexComponent extends UIComponentBase {

    public static final String COMPONENT_TYPE = "ejsf.katexComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public KatexComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
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
        responseWriter.writeAttribute("data-ejsf-katex", "", null);
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
        if (!isRendered()) {
            return;
        }
        ResponseWriter responseWriter = context.getResponseWriter();
        responseWriter.endElement("span");
    }
}
