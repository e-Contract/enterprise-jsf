/*
 * Enterprise JSF project.
 *
 * Copyright 2021-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.output;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(OutputMonthComponent.COMPONENT_TYPE)
public class OutputMonthComponent extends UIOutput {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutputMonthComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.outputMonth";

    public static final String COMPONENT_FAMILY = "ejsf";

    public OutputMonthComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        omitMonthNumber,
    }

    public void setOmitMonthNumber(boolean omitMonthNumber) {
        getStateHelper().put(PropertyKeys.omitMonthNumber, omitMonthNumber);
    }

    public boolean isOmitMonthNumber() {
        return (Boolean) getStateHelper().eval(PropertyKeys.omitMonthNumber, false);
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        if (!isRendered()) {
            return;
        }
        Object value = getValue();
        String monthStr;
        if (value != null) {
            int month;
            if (value instanceof Integer) {
                month = (Integer) value;
            } else if (value instanceof Long) {
                month = ((Long) value).intValue();
            } else {
                LOGGER.error("unsupported type: {}", value.getClass().getName());
                return;
            }
            UIViewRoot viewRoot = context.getViewRoot();
            Locale locale = viewRoot.getLocale();
            try {
                monthStr = Month.of(month).getDisplayName(TextStyle.FULL, locale);
                boolean omitMonthNumber = isOmitMonthNumber();
                if (!omitMonthNumber) {
                    monthStr += " (" + month + ")";
                }
            } catch (DateTimeException ex) {
                monthStr = "invalid month: " + month;
            }
        } else {
            monthStr = "";
        }

        ResponseWriter responseWriter = context.getResponseWriter();
        String clientId = getClientId(context);
        responseWriter.startElement("span", this);
        responseWriter.writeAttribute("id", clientId, "id");

        responseWriter.write(monthStr);

        responseWriter.endElement("span");
    }
}
