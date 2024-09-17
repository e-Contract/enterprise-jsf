/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.browser;

import java.io.IOException;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(BrowserComponent.COMPONENT_TYPE)
public class BrowserComponent extends UIComponentBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrowserComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.browserComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public BrowserComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        name,
    }

    public void setName(String name) {
        getStateHelper().put(PropertyKeys.name, name);
    }

    public String getName() {
        return (String) getStateHelper().get(PropertyKeys.name);
    }

    private Browser getBrowser() {
        FacesContext context = getFacesContext();
        ExternalContext externalContext = context.getExternalContext();
        HttpServletRequest httpServletRequest = (HttpServletRequest) externalContext.getRequest();
        String clientHintsUserAgent = httpServletRequest.getHeader("Sec-CH-UA");
        if (null != clientHintsUserAgent) {
            LOGGER.debug("Sec-CH-UA: {}", clientHintsUserAgent);
            if (clientHintsUserAgent.contains("Edge")) {
                return Browser.EDGE;
            }
            if (clientHintsUserAgent.contains("Chrome")) {
                return Browser.CHROME;
            }
        }
        String userAgent = httpServletRequest.getHeader("User-Agent");
        LOGGER.debug("user agent: {}", userAgent);
        if (userAgent.contains("Edg/")) {
            return Browser.EDGE;
        }
        if (userAgent.contains("Chrome")) {
            return Browser.CHROME;
        }
        if (userAgent.contains("Firefox")) {
            return Browser.FIREFOX;
        }
        if (userAgent.contains("Safari")) {
            return Browser.SAFARI;
        }
        return Browser.UNKNOWN;
    }

    @Override
    public boolean getRendersChildren() {
        if (!isRendered()) {
            return true;
        }
        String browserName = getName();
        Browser browser = Browser.toBrowser(browserName);
        Browser actualBrowser = getBrowser();
        if (actualBrowser.equals(browser)) {
            return false;
        }
        return true;
    }

    @Override
    public void encodeChildren(FacesContext context) throws IOException {
        // do nothing here
    }
}
