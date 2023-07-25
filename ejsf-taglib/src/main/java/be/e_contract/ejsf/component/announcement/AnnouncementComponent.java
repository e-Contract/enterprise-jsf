/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.announcement;

import java.util.HashMap;
import java.util.Map;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.util.LangUtils;
import org.primefaces.util.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(AnnouncementComponent.COMPONENT_TYPE)
public class AnnouncementComponent extends UIComponentBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnouncementComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.announcementComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public AnnouncementComponent() {
        setRendererType(AnnouncementRenderer.RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        name,
        version,
    }

    public String getName() {
        return (String) getStateHelper().eval(PropertyKeys.name, "announcement");
    }

    public void setName(String name) {
        getStateHelper().put(PropertyKeys.name, name);
    }

    public Integer getVersion() {
        return (Integer) getStateHelper().eval(PropertyKeys.version, null);
    }

    public void setVersion(Integer version) {
        getStateHelper().put(PropertyKeys.version, version);
    }

    private String getAcceptedVersionAttribute() {
        String cookieName = getName();
        String acceptedVersionAttribute = AnnouncementComponent.class.getName() + "." + cookieName + ".version";
        return acceptedVersionAttribute;
    }

    public boolean hasAnnouncementAccepted() {
        Integer version = getVersion();
        if (null == version) {
            // nothing to announce here
            return true;
        }
        String acceptedVersionAttribute = getAcceptedVersionAttribute();
        FacesContext facesContext = getFacesContext();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletRequest httpServletRequest = (HttpServletRequest) externalContext.getRequest();
        Integer acceptedVersion = (Integer) httpServletRequest.getAttribute(acceptedVersionAttribute);
        if (null != acceptedVersion) {
            return acceptedVersion.equals(version);
        }
        String cookieName = getName();
        Cookie cookie = (Cookie) externalContext.getRequestCookieMap().get(cookieName);
        if (null == cookie) {
            return false;
        }
        acceptedVersion = Integer.valueOf(cookie.getValue());
        LOGGER.debug("accepted version: {}", acceptedVersion);
        return acceptedVersion.equals(version);
    }

    public void acceptAnnouncement(int retention) {
        String cookieName = getName();
        Integer version = getVersion();
        String acceptedVersionAttribute = getAcceptedVersionAttribute();
        FacesContext facesContext = getFacesContext();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletRequest httpServletRequest = (HttpServletRequest) externalContext.getRequest();

        httpServletRequest.setAttribute(acceptedVersionAttribute, version);

        Map<String, Object> cookieOptions = new HashMap<>();
        String path = externalContext.getRequestContextPath();
        cookieOptions.put("path", LangUtils.isBlank(path) ? "/" : path);
        cookieOptions.put("maxAge", retention);
        cookieOptions.put("httpOnly", Boolean.TRUE);
        ResourceUtils.addResponseCookie(facesContext, cookieName, version.toString(), cookieOptions);
    }

    public Integer getAcceptedVersion() {
        String acceptedVersionAttribute = getAcceptedVersionAttribute();
        FacesContext facesContext = getFacesContext();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletRequest httpServletRequest = (HttpServletRequest) externalContext.getRequest();
        Integer acceptedVersion = (Integer) httpServletRequest.getAttribute(acceptedVersionAttribute);
        if (null != acceptedVersion) {
            return acceptedVersion;
        }
        String cookieName = getName();
        Cookie cookie = (Cookie) externalContext.getRequestCookieMap().get(cookieName);
        if (null == cookie) {
            return null;
        }
        return Integer.valueOf(cookie.getValue());
    }
}
