/*
 * Enterprise JSF project.
 *
 * Copyright 2014-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.output.currency;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesRenderer(componentFamily = OutputCurrencyComponent.COMPONENT_FAMILY, rendererType = OutputCurrencyRenderer.RENDERER_TYPE)
public class OutputCurrencyRenderer extends CoreRenderer {

    public static final String RENDERER_TYPE = "ejsf.outputCurrencyRenderer";

    private static final Logger LOGGER = LoggerFactory.getLogger(OutputCurrencyRenderer.class);

    @Override
    public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
        Float number;
        OutputCurrencyComponent outputCurrencyComponent = (OutputCurrencyComponent) component;
        Object value = outputCurrencyComponent.getValue();
        if (value instanceof Float) {
            number = (Float) value;
        } else if (value instanceof Double) {
            number = ((Double) value).floatValue();
        } else if (value instanceof Long) {
            number = ((Long) value).floatValue();
        } else if (value instanceof Integer) {
            number = ((Integer) value).floatValue();
        } else {
            if (null != value) {
                LOGGER.warn("unsupported value type: {}", value.getClass().getName());
            }
            number = null;
        }
        String currency = outputCurrencyComponent.getCurrency();
        Locale locale = null;
        Object localeAttr = outputCurrencyComponent.getLocale();
        if (null != localeAttr) {
            if (localeAttr instanceof String) {
                locale = new Locale((String) localeAttr);
            }
            if (localeAttr instanceof Locale) {
                locale = (Locale) localeAttr;
            }
        }
        if (locale == null) {
            UIViewRoot viewRoot = facesContext.getViewRoot();
            locale = viewRoot.getLocale();
        }
        String clientId = component.getClientId();
        ResponseWriter responseWriter = facesContext.getResponseWriter();
        responseWriter.startElement("span", component);
        responseWriter.writeAttribute("id", clientId, "id");

        responseWriter.startElement("span", component);
        responseWriter.writeAttribute("id", clientId + ":value", "value");
        String formattedNumber = formatNumber(number, locale);
        if (null != formattedNumber) {
            responseWriter.write(formattedNumber);
        }
        responseWriter.endElement("span");

        responseWriter.startElement("span", component);
        responseWriter.writeAttribute("id", clientId + ":currency", null);
        if (null != number) {
            responseWriter.write(" " + currency);
        }
        responseWriter.endElement("span");

        responseWriter.endElement("span");

        WidgetBuilder widgetBuilder = getWidgetBuilder(facesContext);
        widgetBuilder.init("EJSFOutputCurrency", outputCurrencyComponent);
        widgetBuilder.attr("initialValue", formattedNumber);
        widgetBuilder.attr("currency", currency);
        widgetBuilder.finish();
    }

    private String formatNumber(Float number, Locale locale) throws IOException {
        if (null == number) {
            return null;
        }
        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setRoundingMode(RoundingMode.HALF_UP);
        numberFormat.setMaximumFractionDigits(2);
        return numberFormat.format(number);
    }
}
