/*
 * Enterprise JSF project.
 *
 * Copyright 2021-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.output;

import java.io.IOException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent(OutputTextEditorHtmlComponent.COMPONENT_TYPE)
@ResourceDependencies(value = {
    @ResourceDependency(library = "primefaces", name = "texteditor/texteditor.css")
})
public class OutputTextEditorHtmlComponent extends UIOutput {

    public static final String COMPONENT_TYPE = "ejsf.outputTextEditorHtml";

    public static final String COMPONENT_FAMILY = "ejsf";

    public OutputTextEditorHtmlComponent() {
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

        String clientId = super.getClientId(context);
        responseWriter.startElement("span", this);
        responseWriter.writeAttribute("id", clientId, "id");
        responseWriter.writeAttribute("class", "ql-editor", null);

        Object value = getValue();
        if (null != value) {
            responseWriter.write(getValue().toString());
        }

        responseWriter.endElement("span");
    }
}
