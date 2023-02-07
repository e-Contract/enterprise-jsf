/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.output.enums;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent(OutputEnumComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "ejsf", name = "output-enum.css")
})
public class OutputEnumComponent extends UIOutput {

    public static final String COMPONENT_TYPE = "ejsf.outputEnum";

    public static final String COMPONENT_FAMILY = "ejsf";

    public OutputEnumComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        enums,
    }

    public static class EnumInfo implements Serializable {

        private final String label;
        private final String color;
        private final String background;

        public EnumInfo() {
            this(null, null, null);
        }

        public EnumInfo(String label, String color, String background) {
            this.label = label;
            this.color = color;
            this.background = background;
        }

        public String getLabel() {
            return this.label;
        }

        public String getColor() {
            return this.color;
        }

        public String getBackground() {
            return this.background;
        }
    }

    public Map<String, EnumInfo> getEnums() {
        return (Map<String, EnumInfo>) getStateHelper().eval(PropertyKeys.enums);
    }

    public void setEnums(Map<String, EnumInfo> enums) {
        getStateHelper().put(PropertyKeys.enums, enums);
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        Enum value = (Enum) getValue();
        Map<String, EnumInfo> enums = getEnums();
        String label = null;
        String style = "";
        if (null != value) {
            if (null != enums) {
                EnumInfo enumInfo = enums.get(value.name());
                if (null != enumInfo) {
                    label = enumInfo.getLabel();
                    String color = enumInfo.getColor();
                    if (null != color) {
                        style += "color: " + color + "; border-color: " + color + ";";
                    }
                    String background = enumInfo.getBackground();
                    if (null != background) {
                        style += "background: " + background + ";";
                    }
                }
            }
            if (label == null) {
                label = value.toString();
            }
        } else {
            label = "";
        }
        String clientId = super.getClientId(context);
        ResponseWriter responseWriter = context.getResponseWriter();
        responseWriter.startElement("span", this);
        responseWriter.writeAttribute("id", clientId, "id");
        responseWriter.writeAttribute("class", "output-enum", null);
        if (!style.isEmpty()) {
            responseWriter.writeAttribute("style", style, null);
        }
        responseWriter.write(label);
        responseWriter.endElement("span");
    }
}
