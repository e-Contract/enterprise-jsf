/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.insertfacet;

import java.io.IOException;
import java.util.List;
import javax.faces.FacesWrapper;
import javax.faces.component.UIComponent;
import javax.faces.event.PostAddToViewEvent;
import javax.faces.event.SystemEventListener;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InsertFacetTagHandler extends TagHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(InsertFacetTagHandler.class);

    private final TagAttribute nameTagAttribute;

    private final TagAttribute targetNameTagAttribute;

    public InsertFacetTagHandler(final TagConfig config) {
        super(config);
        this.nameTagAttribute = getRequiredAttribute("name");
        this.targetNameTagAttribute = getAttribute("targetName");
    }

    /**
     * Seems like Mojarra registers our listeners multiple times. Hence we
     * detect and prevent this.
     *
     * @param component
     * @param name
     * @param targetName
     * @return
     */
    private boolean isAlreadyRegistered(UIComponent component, String name, String targetName) {
        List<SystemEventListener> listeners = component.getListenersForEventClass(PostAddToViewEvent.class);
        if (null == listeners) {
            return false;
        }
        for (SystemEventListener listener : listeners) {
            if (!(listener instanceof FacesWrapper)) {
                continue;
            }
            FacesWrapper facesWrapper = (FacesWrapper) listener;
            Object wrapped = facesWrapper.getWrapped();
            if (!(wrapped instanceof InsertFacetListener)) {
                continue;
            }
            InsertFacetListener insertFacetListener = (InsertFacetListener) wrapped;
            if (!name.equals(insertFacetListener.getName())) {
                continue;
            }
            if (targetName == null && insertFacetListener.getTargetName() == null) {
                return true;
            }
            if (targetName == null) {
                continue;
            }
            if (targetName.equals(insertFacetListener.getTargetName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void apply(FaceletContext context, UIComponent parent) throws IOException {
        String name = this.nameTagAttribute.getValue(context);
        String targetName;
        if (null != this.targetNameTagAttribute) {
            targetName = this.targetNameTagAttribute.getValue(context);
        } else {
            targetName = null;
        }
        if (!isAlreadyRegistered(parent, name, targetName)) {
            LOGGER.debug("subscribeToEvent");
            parent.subscribeToEvent(PostAddToViewEvent.class, new InsertFacetListener(name, targetName));
        }
    }
}
