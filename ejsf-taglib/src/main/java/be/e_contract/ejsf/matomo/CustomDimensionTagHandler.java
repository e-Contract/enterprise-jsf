/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.matomo;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagException;
import javax.faces.view.facelets.TagHandler;

public class CustomDimensionTagHandler extends TagHandler {

    public CustomDimensionTagHandler(TagConfig tagConfig) {
        super(tagConfig);
    }

    @Override
    public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException {
        if (!ComponentHandler.isNew(parent)) {
            return;
        }
        if (!(parent instanceof TrackingComponent)) {
            throw new TagException(this.tag, "matomoCustomDimension can only be attached to matomoTracking components.");
        }
        boolean enabled;
        TagAttribute enabledTagAttribute = getAttribute("enabled");
        if (null != enabledTagAttribute) {
            enabled = (Boolean) enabledTagAttribute.getObject(faceletContext, Boolean.class);
        } else {
            enabled = true;
        }
        if (!enabled) {
            return;
        }
        int dimension = getRequiredAttribute("dimension").getInt(faceletContext);
        String value = getRequiredAttribute("value").getValue(faceletContext);
        TrackingComponent trackingComponent = (TrackingComponent) parent;
        List<CustomDimension> customDimensions = trackingComponent.getCustomDimensions();
        if (null == customDimensions) {
            customDimensions = new LinkedList<>();
            trackingComponent.setCustomDimensions(customDimensions);
        }
        CustomDimension customDimension = new CustomDimension(dimension, value);
        customDimensions.add(customDimension);
    }
}
