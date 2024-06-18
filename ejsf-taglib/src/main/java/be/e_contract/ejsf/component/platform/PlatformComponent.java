/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.platform;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(PlatformComponent.COMPONENT_TYPE)
public class PlatformComponent extends UIComponentBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.platformComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public PlatformComponent() {
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

    public boolean match(Platform platform) {
        if (platform == Platform.UNKNOWN) {
            return true;
        }
        String platformName = getName();
        Platform thisPlatform = Platform.toPlatform(platformName);
        return platform.equals(thisPlatform);
    }
}
