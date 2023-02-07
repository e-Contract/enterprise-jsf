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
import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent(OutputEnumComponent.COMPONENT_TYPE)
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

        private final String name;
        private String label;

        public EnumInfo() {
            this(null, null);
        }

        public EnumInfo(String name, String label) {
            this.name = name;
            this.label = label;
        }

        public String getName() {
            return this.name;
        }

        public String getLabel() {
            return this.label;
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
        String strValue = null;
        if (null != value) {
            if (null != enums) {
                EnumInfo enumInfo = enums.get(value.name());
                if (null != enumInfo) {
                    strValue = enumInfo.getLabel();
                }
            }
            if (strValue == null) {
                strValue = value.toString();
            }
        } else {
            strValue = "";
        }
        String clientId = super.getClientId(context);
        ResponseWriter responseWriter = context.getResponseWriter();
        responseWriter.startElement("span", this);
        responseWriter.writeAttribute("id", clientId, "id");
        responseWriter.write(strValue);
        responseWriter.endElement("span");
    }
}
