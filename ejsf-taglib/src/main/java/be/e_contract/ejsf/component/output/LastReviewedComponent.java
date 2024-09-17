/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.output;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent(LastReviewedComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "ejsf", name = "last-reviewed.css")
})
public class LastReviewedComponent extends UIOutput {

    public static final String COMPONENT_TYPE = "ejsf.lastReviewed";

    public static final String COMPONENT_FAMILY = "ejsf";

    public LastReviewedComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        pattern,
        unknownMessage,
        maxAgeWarning,
        maxAgeError,
        okIcon,
        warningIcon,
        errorIcon,
    }

    public String getPattern() {
        return (String) getStateHelper().eval(PropertyKeys.pattern, "dd/MM/yyyy HH:mm");
    }

    public void setPattern(String pattern) {
        getStateHelper().put(PropertyKeys.pattern, pattern);
    }

    public String getUnknownMessage() {
        return (String) getStateHelper().eval(PropertyKeys.unknownMessage, "Unknown");
    }

    public void setUnknownMessage(String unknownMessage) {
        getStateHelper().put(PropertyKeys.unknownMessage, unknownMessage);
    }

    public String getMaxAgeWarning() {
        return (String) getStateHelper().eval(PropertyKeys.maxAgeWarning, "P3M");
    }

    public void setMaxAgeWarning(String maxAgeWarning) {
        getStateHelper().put(PropertyKeys.maxAgeWarning, maxAgeWarning);
    }

    public String getMaxAgeError() {
        return (String) getStateHelper().eval(PropertyKeys.maxAgeError, "P6M");
    }

    public void setMaxAgeError(String maxAgeError) {
        getStateHelper().put(PropertyKeys.maxAgeError, maxAgeError);
    }

    public String getOkIcon() {
        return (String) getStateHelper().eval(PropertyKeys.okIcon);
    }

    public void setOkIcon(String okIcon) {
        getStateHelper().put(PropertyKeys.okIcon, okIcon);
    }

    public String getWarningIcon() {
        return (String) getStateHelper().eval(PropertyKeys.warningIcon);
    }

    public void setWarningIcon(String warningIcon) {
        getStateHelper().put(PropertyKeys.warningIcon, warningIcon);
    }

    public String getErrorIcon() {
        return (String) getStateHelper().eval(PropertyKeys.errorIcon);
    }

    public void setErrorIcon(String errorIcon) {
        getStateHelper().put(PropertyKeys.errorIcon, errorIcon);
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        if (!isRendered()) {
            return;
        }
        LocalDateTime localDateTime = (LocalDateTime) getValue();
        String pattern = getPattern();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        String value;
        String classValue;
        String icon;
        if (null != localDateTime) {
            value = dateTimeFormatter.format(localDateTime);
            Period maxAgeWarning = Period.parse(getMaxAgeWarning());
            Period maxAgeError = Period.parse(getMaxAgeError());
            LocalDateTime now = LocalDateTime.now();
            if (localDateTime.isBefore(now.minus(maxAgeError))) {
                classValue = "ejsf-last-reviewed ejsf-last-reviewed-error";
                icon = getErrorIcon();
            } else if (localDateTime.isBefore(now.minus(maxAgeWarning))) {
                classValue = "ejsf-last-reviewed ejsf-last-reviewed-warning";
                icon = getWarningIcon();
            } else {
                classValue = "ejsf-last-reviewed ejsf-last-reviewed-ok";
                icon = getOkIcon();
            }
        } else {
            value = getUnknownMessage();
            classValue = "ejsf-last-reviewed ejsf-last-reviewed-unknown";
            icon = null;
        }

        ResponseWriter responseWriter = context.getResponseWriter();
        String clientId = super.getClientId(context);
        responseWriter.startElement("span", this);
        responseWriter.writeAttribute("id", clientId, "id");
        responseWriter.writeAttribute("class", classValue, null);
        if (null != icon) {
            responseWriter.startElement("i", this);
            responseWriter.writeAttribute("class", icon, "icon");
            responseWriter.writeAttribute("style", "margin-right: 5px;", null);
            responseWriter.endElement("i");
        }
        responseWriter.write(value);
        responseWriter.endElement("span");
    }
}
