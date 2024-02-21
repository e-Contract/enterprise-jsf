/*
 * Enterprise JSF project.
 *
 * Copyright 2021-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.output;

import java.io.IOException;
import java.util.Locale;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent(OutputLanguageComponent.COMPONENT_TYPE)
public class OutputLanguageComponent extends UIOutput {

    public static final String COMPONENT_TYPE = "ejsf.outputLanguage";

    public static final String COMPONENT_FAMILY = "ejsf";

    public OutputLanguageComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
        ResponseWriter responseWriter = context.getResponseWriter();

        String clientId = super.getClientId(context);
        responseWriter.startElement("span", this);
        responseWriter.writeAttribute("id", clientId, "id");

        String language = (String) getValue();
        if (!UIInput.isEmpty(language)) {
            Locale locale = new Locale.Builder().setLanguage(language).build();
            String displayLanguage = locale.getDisplayLanguage(locale);
            responseWriter.write(displayLanguage);
        }

        responseWriter.endElement("span");
    }
}
