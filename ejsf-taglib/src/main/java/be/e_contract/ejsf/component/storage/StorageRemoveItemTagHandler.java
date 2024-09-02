/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.storage;

import java.io.IOException;
import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagException;
import javax.faces.view.facelets.TagHandler;

public class StorageRemoveItemTagHandler extends TagHandler {

    public StorageRemoveItemTagHandler(TagConfig tagConfig) {
        super(tagConfig);
    }

    @Override
    public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException {
        if (parent == null) {
            return;
        }
        if (!ComponentHandler.isNew(parent)) {
            return;
        }
        if (!(parent instanceof ActionSource)) {
            throw new TagException(this.tag, "Can only be attached to ActionSource components.");
        }
        ActionSource actionSource = (ActionSource) parent;
        TagAttribute typeTagAttribute = getAttribute("type");
        String type;
        if (null != typeTagAttribute) {
            type = typeTagAttribute.getValue();
        } else {
            type = "local";
        }
        String name = getRequiredAttribute("name").getValue();
        StorageRemoveItemActionListener actionListener = new StorageRemoveItemActionListener(type, name);
        actionSource.addActionListener(actionListener);
    }
}
