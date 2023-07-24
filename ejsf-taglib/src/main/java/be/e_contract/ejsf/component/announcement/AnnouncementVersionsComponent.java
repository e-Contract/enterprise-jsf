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

@FacesComponent(AnnouncementVersionsComponent.COMPONENT_TYPE)
public class AnnouncementVersionsComponent extends UIData {

    public static final String COMPONENT_TYPE = "ejsf.announcementVersionsComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public AnnouncementVersionsComponent() {
        setRendererType(AnnouncementVersionsRenderer.RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public Object getValue() {
        AnnouncementComponent announcementComponent = findAnnouncementComponent(this);
        if (null == announcementComponent) {
            return null;
        }
        Integer initialVersion = announcementComponent.getAcceptedVersion();
        if (null == initialVersion) {
            initialVersion = 0;
        } else {
            initialVersion++;
        }
        Integer currentVersion = announcementComponent.getVersion();
        if (null == currentVersion) {
            currentVersion = 0;
        }
        List<Integer> versions = new LinkedList<>();
        for (int version = initialVersion; version <= currentVersion; version++) {
            versions.add(version);
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
