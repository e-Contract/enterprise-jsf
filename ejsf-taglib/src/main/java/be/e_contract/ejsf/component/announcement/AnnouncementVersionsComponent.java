/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.announcement;

import java.util.LinkedList;
import java.util.List;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(AnnouncementVersionsComponent.COMPONENT_TYPE)
public class AnnouncementVersionsComponent extends UIData {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnouncementVersionsComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.announcementVersionsComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public AnnouncementVersionsComponent() {
        setRendererType(AnnouncementVersionsRenderer.RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        oldestVersion,
        order
    }

    public Integer getOldestVersion() {
        return (Integer) getStateHelper().eval(PropertyKeys.oldestVersion, null);
    }

    public void setOldestVersion(Integer oldestVersion) {
        getStateHelper().put(PropertyKeys.oldestVersion, oldestVersion);
    }

    public String getOrder() {
        return (String) getStateHelper().eval(PropertyKeys.order, "asc");
    }

    public void setOrder(String order) {
        getStateHelper().put(PropertyKeys.order, order);
    }

    @Override
    public Object getValue() {
        AnnouncementComponent announcementComponent = findAnnouncementComponent(this);
        if (null == announcementComponent) {
            LOGGER.warn("AnnouncementComponent not found.");
            return null;
        }
        Integer initialVersion = announcementComponent.getAcceptedVersion();
        if (null == initialVersion) {
            initialVersion = 0;
        } else {
            initialVersion++;
        }
        Integer oldestVersion = getOldestVersion();
        if (null != oldestVersion) {
            if (initialVersion < oldestVersion) {
                initialVersion = oldestVersion;
            }
        }

        Integer currentVersion = announcementComponent.getVersion();
        if (null == currentVersion) {
            currentVersion = 0;
        }
        List<Integer> versions = new LinkedList<>();
        String order = getOrder();
        if ("desc".equals(order)) {
            for (int version = currentVersion; version >= initialVersion; version--) {
                versions.add(version);
            }
        } else {
            for (int version = initialVersion; version <= currentVersion; version++) {
                versions.add(version);
            }
        }
        return versions;
    }

    private AnnouncementComponent findAnnouncementComponent(UIComponent component) {
        while (component != null) {
            if (component instanceof AnnouncementComponent) {
                return (AnnouncementComponent) component;
            }
            component = component.getParent();
        }
        return null;
    }
}
