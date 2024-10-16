/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import java.io.Serializable;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@ViewScoped
public class LinkController implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkController.class);

    private String canonicalUrl;

    public void pageAction() {
        LOGGER.debug("pageAction");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        String scheme = externalContext.getRequestScheme();
        int port = externalContext.getRequestServerPort();
        String serverName = externalContext.getRequestServerName();
        String contextPath = externalContext.getRequestContextPath();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(scheme).append("://").append(serverName);
        if ("http".equals(scheme)) {
            if (port != 80) {
                stringBuilder.append(":").append(port);
            }
        } else if ("https".equals(scheme)) {
            if (port != 443) {
                stringBuilder.append(":").append(port);
            }
        }
        stringBuilder.append(contextPath).append("/link.xhtml");
        this.canonicalUrl = stringBuilder.toString();
    }

    public String getCanonicalUrl() {
        LOGGER.debug("getCanonicalUrl: {}", this.canonicalUrl);
        return this.canonicalUrl;
    }
}
