/*
 * Enterprise JSF project.
 *
 * Copyright 2021-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.output;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.apache.commons.lang3.StringUtils;

@FacesComponent(OutputDayOfWeekComponent.COMPONENT_TYPE)
public class OutputDayOfWeekComponent extends UIOutput {

    public static final String COMPONENT_TYPE = "ejsf.outputDayOfWeek";

    public static final String COMPONENT_FAMILY = "ejsf";

    public OutputDayOfWeekComponent() {
        setRendererType(null);
    }

    enum PropertyKeys {
        styleClass,
        style
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public void setStyleClass(String styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, styleClass);
    }

    public String getStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.styleClass);
    }

    public String getStyle() {
        return (String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(String style) {
        getStateHelper().put(PropertyKeys.style, style);
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
        ResponseWriter responseWriter = context.getResponseWriter();

        DayOfWeek dayOfWeek = (DayOfWeek) getValue();
        String localizedDayOfWeek = localizeDay(dayOfWeek);
        String styleClass = getStyleClass();

        String clientId = super.getClientId(context);

        responseWriter.startElement("span", this);
        responseWriter.writeAttribute("id", clientId, "id");
        if (!UIInput.isEmpty(styleClass)) {
            responseWriter.writeAttribute("class", styleClass, "styleClass");
        }
        String style = getStyle();
        if (null != style) {
            responseWriter.writeAttribute("style", style, "style");
        }
        responseWriter.write(localizedDayOfWeek);
        responseWriter.endElement("span");
    }

    private String localizeDay(DayOfWeek dayOfWeek) {
        if (null == dayOfWeek) {
            return "";
        }
        Locale locale = getFacesContext().getViewRoot().getLocale();
        if (null == locale) {
            return capitalize(dayOfWeek.name());
        } else {
            return capitalize(dayOfWeek.getDisplayName(TextStyle.FULL_STANDALONE, locale));
        }
    }

    private String capitalize(String string) {
        return StringUtils.capitalize(StringUtils.lowerCase(string));
    }
}
