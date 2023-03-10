/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.viewlogs;

import be.e_contract.ejsf.Environment;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.faces.application.Application;
import javax.faces.application.ProjectStage;
import javax.faces.application.Resource;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewLogsResource extends Resource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ViewLogsResource.class);

    private static final String REQUIRED_ROLE_CONTEXT_PARAM = "ejsf.viewLogs.REQUIRED_ROLE";

    public ViewLogsResource(String resourceName, String libraryName) {
        setResourceName(resourceName);
        setLibraryName(libraryName);
    }

    @Override
    public String getContentType() {
        LOGGER.debug("getContentType");
        return "text/plain";
    }

    @Override
    public InputStream getInputStream() throws IOException {
        LOGGER.debug("getInputStream");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        String requiredRole = externalContext.getInitParameter(REQUIRED_ROLE_CONTEXT_PARAM);
        if (UIInput.isEmpty(requiredRole)) {
            LOGGER.warn("context-param " + REQUIRED_ROLE_CONTEXT_PARAM + " not defined!");
            if (!facesContext.isProjectStage(ProjectStage.Development)) {
                String message = "RBAC error.";
                LOGGER.warn(message);
                return new ByteArrayInputStream(message.getBytes());
            } else {
                LOGGER.warn("configure " + REQUIRED_ROLE_CONTEXT_PARAM + " for production usage");
            }
        } else {
            LOGGER.debug("required role: {}", requiredRole);
            if (!externalContext.isSecure()) {
                String message = "Not secure.";
                LOGGER.warn(message);
                return new ByteArrayInputStream(message.getBytes());
            }
            if (!externalContext.isUserInRole(requiredRole)) {
                String message = "RBAC error.";
                LOGGER.warn(message);
                return new ByteArrayInputStream(message.getBytes());
            }
        }
        String resourceName = this.getResourceName();
        if (resourceName.endsWith(".xhtml")) {
            // OmniFaces somehow adds .xhtml to the resource name
            resourceName = resourceName.substring(0, resourceName.indexOf(".xhtml"));
        }
        File logFile = ViewLogsManager.findLogFile(resourceName);
        if (null == logFile) {
            String message = "Unknown logfile: " + resourceName;
            return new ByteArrayInputStream(message.getBytes());
        }
        return new FileInputStream(logFile);
    }

    @Override
    public Map<String, String> getResponseHeaders() {
        LOGGER.debug("getResponseHeaders");
        Map<String, String> responseHeaders = new HashMap<>();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        Map<String, String> requestParameters = externalContext.getRequestParameterMap();
        String mode = requestParameters.get("mode");
        if ("download".equals(mode)) {
            responseHeaders.put("Content-Disposition", "attachment; filename:\"" + getResourceName() + "\"");
        }
        return responseHeaders;
    }

    @Override
    public String getRequestPath() {
        LOGGER.debug("getRequestPath");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Application application = facesContext.getApplication();
        ViewHandler viewHandler = application.getViewHandler();
        String resourcePath = Environment.getResourceHandlerResourceIdentifier() + "/" + getResourceName() + ".xhtml?ln=" + getLibraryName();
        return viewHandler.getResourceURL(facesContext, resourcePath);
    }

    @Override
    public URL getURL() {
        LOGGER.debug("getURL");
        return null;
    }

    @Override
    public boolean userAgentNeedsUpdate(FacesContext context) {
        LOGGER.debug("userAgentNeedsUpdate");
        return true;
    }
}
