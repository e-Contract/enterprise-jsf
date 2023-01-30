/*
 * Enterprise JSF project.
 *
 * Copyright 2014-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.output;

import java.io.IOException;
import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent(OutputBooleanComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "ejsf", name = "output-boolean.css")
})
public class OutputBooleanComponent extends UIOutput {

    public static final String COMPONENT_TYPE = "ejsf.outputBoolean";

    public static final String COMPONENT_FAMILY = "ejsf";

    public OutputBooleanComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        Boolean value = (Boolean) getValue();
        if (null == value) {
            return;
        }
        Application application = context.getApplication();
        ResourceBundle resourceBundle = application.getResourceBundle(context, "ejsfMessages");
        String output;
        String classValue = "output-boolean output-boolean-" + Boolean.toString(value);
        if (value) {
            output = resourceBundle.getString("yes");
        } else {
            output = resourceBundle.getString("no");
        }
        ResponseWriter responseWriter = context.getResponseWriter();
        String clientId = super.getClientId(context);
        responseWriter.startElement("span", this);
        responseWriter.writeAttribute("id", clientId, "id");
        responseWriter.writeAttribute("class", classValue, null);
        responseWriter.write(output);
        responseWriter.endElement("span");
    }
}