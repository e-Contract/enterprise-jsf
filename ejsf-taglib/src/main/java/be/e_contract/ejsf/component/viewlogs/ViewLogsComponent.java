/*
 * Enterprise JSF project.
 *
 * Copyright 2023-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.viewlogs;

import be.e_contract.ejsf.component.output.currency.OutputCurrencyComponent;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import javax.faces.application.Application;
import javax.faces.application.Resource;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.application.ResourceHandler;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(ViewLogsComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "ejsf", name = "view-logs.css")
})
public class ViewLogsComponent extends UIComponentBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ViewLogsComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.viewLogsComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public ViewLogsComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public enum PropertyKeys {
        style,
        styleClass,
        newTab,
        includes,
        excludes,
        dateTimePattern
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        if (!isRendered()) {
            return;
        }
        ResponseWriter responseWriter = context.getResponseWriter();
        String clientId = getClientId(context);
        responseWriter.startElement("div", this);
        responseWriter.writeAttribute("id", clientId, "id");

        String style = getStyle();
        if (null != style) {
            responseWriter.writeAttribute("style", style, "style");
        }
        String styleClass = getStyleClass();
        if (null != styleClass) {
            responseWriter.writeAttribute("class", styleClass, "styleClass");
        }

        Application application = context.getApplication();
        ResourceHandler resourceHandler = application.getResourceHandler();

        responseWriter.startElement("table", this);
        responseWriter.writeAttribute("class", "ejsf-view-logs", null);
        responseWriter.startElement("tr", this);

        responseWriter.startElement("th", this);
        responseWriter.write("Filename");
        responseWriter.endElement("th");

        responseWriter.startElement("th", this);
        responseWriter.write("Size");
        responseWriter.endElement("th");

        responseWriter.startElement("th", this);
        responseWriter.write("Last Modified");
        responseWriter.endElement("th");

        responseWriter.endElement("tr");

        DateTimeFormatter dateTimeFormatter;
        String dateTimePattern = getDateTimePattern();
        if (null != dateTimePattern) {
            dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern);
        } else {
            dateTimeFormatter = null;
        }

        List<String> includes = getIncludes();
        List<String> excludes = getExcludes();
        List<File> logFiles = ViewLogsManager.getLogFiles(includes, excludes);
        AuthorizationCode authorizationCode = new AuthorizationCode(context);
        for (File logFile : logFiles) {
            responseWriter.startElement("tr", this);

            responseWriter.startElement("td", this);
            responseWriter.startElement("a", this);
            boolean newTab = isNewTab();
            if (newTab) {
                responseWriter.writeAttribute("target", "_blank", "newTab");
            }
            Resource resource = resourceHandler.createResource(logFile.getName(), ViewLogsResourceHandler.LIBRARY_NAME);
            if (null == resource) {
                LOGGER.warn("missing ViewLogsResourceHandler configuration");
                throw new IOException("missing ViewLogsResourceHandler configuration");
            }
            String resourceRequestPath = resource.getRequestPath();
            String code = authorizationCode.generateCode(logFile.getName());
            resourceRequestPath += "&code=" + code;
            responseWriter.writeAttribute("href", resourceRequestPath, null);
            responseWriter.writeText(logFile.getName(), null);
            responseWriter.endElement("a");
            responseWriter.write(" ");
            responseWriter.startElement("a", this);
            responseWriter.writeAttribute("href", resourceRequestPath + "&mode=download", null);
            responseWriter.startElement("i", this);
            responseWriter.writeAttribute("class", "pi pi-download", null);
            responseWriter.endElement("i");
            responseWriter.endElement("a");

            responseWriter.endElement("td");

            responseWriter.startElement("td", this);
            responseWriter.write(FileUtils.byteCountToDisplaySize(logFile.length()));
            responseWriter.endElement("td");

            responseWriter.startElement("td", this);
            if (null != dateTimeFormatter) {
                Date lastModified = new Date(logFile.lastModified());
                LocalDateTime localDateTime = LocalDateTime.ofInstant(lastModified.toInstant(), ZoneId.systemDefault());
                responseWriter.write(dateTimeFormatter.format(localDateTime));
            } else {
                responseWriter.write(new Date(logFile.lastModified()).toString());
            }
            responseWriter.endElement("td");

            responseWriter.endElement("tr");
        }
        responseWriter.endElement("table");
        if (logFiles.isEmpty()) {
            responseWriter.write("No log files available.");
        }

        responseWriter.endElement("div");
    }

    public String getStyle() {
        return (String) getStateHelper().eval(OutputCurrencyComponent.PropertyKeys.style, null);
    }

    public void setStyle(String style) {
        getStateHelper().put(OutputCurrencyComponent.PropertyKeys.style, style);
    }

    public String getStyleClass() {
        return (String) getStateHelper().eval(OutputCurrencyComponent.PropertyKeys.styleClass, null);
    }

    public void setStyleClass(String styleClass) {
        getStateHelper().put(OutputCurrencyComponent.PropertyKeys.styleClass, styleClass);
    }

    public boolean isNewTab() {
        return (Boolean) getStateHelper().eval(PropertyKeys.newTab, false);
    }

    public void setNewTab(boolean newTab) {
        getStateHelper().put(PropertyKeys.newTab, newTab);
    }

    public void setIncludes(List<String> includes) {
        getStateHelper().put(PropertyKeys.includes, includes);
    }

    public List<String> getIncludes() {
        return (List<String>) getStateHelper().eval(PropertyKeys.includes);
    }

    public void setExcludes(List<String> excludes) {
        getStateHelper().put(PropertyKeys.excludes, excludes);
    }

    public List<String> getExcludes() {
        return (List<String>) getStateHelper().eval(PropertyKeys.excludes);
    }

    public void setDateTimePattern(String dateTimePattern) {
        getStateHelper().put(PropertyKeys.dateTimePattern, dateTimePattern);
    }

    public String getDateTimePattern() {
        return (String) getStateHelper().eval(PropertyKeys.dateTimePattern);
    }
}
