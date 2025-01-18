/*
 * Enterprise JSF project.
 *
 * Copyright 2014-2025 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.output;

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

    enum PropertyKeys {
        reverse,
        colors,
    }

    public boolean isReverse() {
        return (Boolean) getStateHelper().eval(PropertyKeys.reverse, false);
    }

    public void setReverse(boolean reverse) {
        getStateHelper().put(PropertyKeys.reverse, reverse);
    }

    public boolean isColors() {
        return (Boolean) getStateHelper().eval(PropertyKeys.colors, true);
    }

    public void setColors(boolean colors) {
        getStateHelper().put(PropertyKeys.colors, colors);
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        if (!isRendered()) {
            return;
        }
        Boolean value = (Boolean) getValue();
        String output;
        String classValue;
        if (null != value) {
            Application application = context.getApplication();
            ResourceBundle resourceBundle = application.getResourceBundle(context, "ejsfMessages");
            if (value) {
                output = resourceBundle.getString("yes");
            } else {
                output = resourceBundle.getString("no");
            }
            boolean colors = isColors();
            if (colors) {
                boolean reverse = isReverse();
                classValue = "ejsf-output-boolean ejsf-output-boolean-" + Boolean.toString(value ^ reverse);
            } else {
                classValue = "ejsf-output-boolean";
            }
        } else {
            output = "";
            classValue = null;
        }
        ResponseWriter responseWriter = context.getResponseWriter();
        String clientId = getClientId(context);
        responseWriter.startElement("span", this);
        responseWriter.writeAttribute("id", clientId, "id");
        if (null != classValue) {
            responseWriter.writeAttribute("class", classValue, null);
        }
        responseWriter.write(output);
        responseWriter.endElement("span");
    }
}
