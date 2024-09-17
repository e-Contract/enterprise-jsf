/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.platform;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(PlatformsComponent.COMPONENT_TYPE)
public class PlatformsComponent extends UIComponentBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformsComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.platformsComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public PlatformsComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    private Platform getPlatform(FacesContext context) {
        ExternalContext externalContext = context.getExternalContext();
        HttpServletRequest httpServletRequest = (HttpServletRequest) externalContext.getRequest();
        String uaPlatform = httpServletRequest.getHeader("Sec-CH-UA-Platform");
        if (null != uaPlatform) {
            LOGGER.debug("Sec-CH-UA-Platform: {}", uaPlatform);
            if (uaPlatform.equals("\"Windows\"")) {
                return Platform.WINDOWS;
            }
            if (uaPlatform.equals("\"macOS\"")) {
                return Platform.MACOS;
            }
            if (uaPlatform.equals("\"Linux\"")) {
                return Platform.LINUX;
            }
            if (uaPlatform.equals("\"FreeBSD\"")) {
                return Platform.LINUX;
            }
        }
        String userAgent = httpServletRequest.getHeader("User-Agent");
        LOGGER.debug("user agent: {}", userAgent);
        if (null != userAgent) {
            if (userAgent.contains("Win")) {
                return Platform.WINDOWS;
            }
            if (userAgent.contains("Mac")) {
                return Platform.MACOS;
            }
            if (userAgent.contains("Linux")) {
                return Platform.LINUX;
            }
            if (userAgent.contains("FreeBSD")) {
                return Platform.FREEBSD;
            }
        }
        return Platform.UNKNOWN;
    }

    private List<PlatformComponent> getMatchingPlatformComponents(Platform platform) {
        List<UIComponent> children = getChildren();
        List<PlatformComponent> platformComponents = new LinkedList<>();
        for (UIComponent component : children) {
            if (!(component instanceof PlatformComponent)) {
                continue;
            }
            PlatformComponent platformComponent = (PlatformComponent) component;
            if (platformComponent.match(platform)) {
                platformComponents.add(platformComponent);
            }
        }
        if (platformComponents.isEmpty()) {
            // worst-case we show everything
            for (UIComponent component : children) {
                if (!(component instanceof PlatformComponent)) {
                    continue;
                }
                PlatformComponent platformComponent = (PlatformComponent) component;
                platformComponents.add(platformComponent);
            }
        }
        return platformComponents;
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(FacesContext context) throws IOException {
        if (!isRendered()) {
            return;
        }
        Platform platform = getPlatform(context);
        List<PlatformComponent> platformComponents = getMatchingPlatformComponents(platform);
        for (PlatformComponent platformComponent : platformComponents) {
            platformComponent.encodeAll(context);
        }
    }
}
