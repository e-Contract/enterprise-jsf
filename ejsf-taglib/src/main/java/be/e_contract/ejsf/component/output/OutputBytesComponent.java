/*
 * Enterprise JSF project.
 *
 * Copyright 2021-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.output;

import java.io.IOException;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.apache.commons.io.FileUtils;

@FacesComponent(OutputBytesComponent.COMPONENT_TYPE)
public class OutputBytesComponent extends UIOutput {

    public static final String COMPONENT_TYPE = "ejsf.outputBytes";

    public static final String COMPONENT_FAMILY = "ejsf";

    public OutputBytesComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public enum PropertyKeys {
        style,
        styleClass
    }

    public String getStyle() {
        return (String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(String style) {
        getStateHelper().put(PropertyKeys.style, style);
    }

    public String getStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(String styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, styleClass);
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        if (!isRendered()) {
            return;
        }
        Long value = (Long) getValue();
        String formattedBytes;
        if (null != value) {
            // next is to also make it work on commons-io < 2.12.0
            long longValue = value;
            formattedBytes = FileUtils.byteCountToDisplaySize(longValue);
        } else {
            formattedBytes = "";
        }
        ResponseWriter responseWriter = context.getResponseWriter();
        String clientId = getClientId(context);
        responseWriter.startElement("span", this);
        responseWriter.writeAttribute("id", clientId, "id");

        String style = getStyle();
        if (null != style) {
            responseWriter.writeAttribute("style", style, "style");
        }
        String styleClass = getStyleClass();
        if (null != styleClass) {
            responseWriter.writeAttribute("class", styleClass, "styleClass");
        }

        responseWriter.write(formattedBytes);
        responseWriter.endElement("span");
    }
}
