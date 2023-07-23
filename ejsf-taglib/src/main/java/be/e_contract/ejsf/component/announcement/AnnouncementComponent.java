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

@FacesComponent(AnnouncementComponent.COMPONENT_TYPE)
public class AnnouncementComponent extends UIComponentBase {

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
        return (Integer) getStateHelper().eval(PropertyKeys.version, 0);
    }

    public void setVersion(Integer version) {
        getStateHelper().put(PropertyKeys.version, version);
    }

    public boolean hasAnnouncementAccepted() {
        String cookieName = getName();
        Integer version = getVersion();
        String acceptedVersionAttribute = AnnouncementComponent.class.getName() + "." + cookieName + ".version";
        FacesContext facesContext = getFacesContext();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletRequest httpServletRequest = (HttpServletRequest) externalContext.getRequest();
        Integer acceptedVersion = (Integer) httpServletRequest.getAttribute(acceptedVersionAttribute);
        if (null != acceptedVersion) {
            if (null == version) {
                return true;
            }
            return acceptedVersion.equals(version);
        }
        Cookie cookie = (Cookie) externalContext.getRequestCookieMap().get(cookieName);
        if (null == cookie) {
            return false;
        }
        acceptedVersion = Integer.valueOf(cookie.getValue());
        return acceptedVersion.equals(version);
    }

    public void acceptAnnouncement(int retention) {
        String cookieName = getName();
        Integer version = getVersion();
        String acceptedVersionAttribute = AnnouncementComponent.class.getName() + "." + cookieName + ".version";
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
}
