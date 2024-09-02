/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.storage;

import java.io.IOException;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagException;
import javax.faces.view.facelets.TagHandler;

public class StorageGetItemTagHandler extends TagHandler {

    public StorageGetItemTagHandler(TagConfig tagConfig) {
        super(tagConfig);
    }

    @Override
    public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException {
        if (!ComponentHandler.isNew(parent)) {
            return;
        }
        if (!(parent instanceof StorageGetItemsComponent)) {
            throw new TagException(this.tag, "StorageGetItem can only be attached to StorageGetItems components.");
        }

        TagAttribute typeTagAttribute = getAttribute("type");
        String type;
        if (null != typeTagAttribute) {
            type = typeTagAttribute.getValue();
        } else {
            type = "local";
        }

        String name = getRequiredAttribute("name").getValue();
        ValueExpression valueExpression = getRequiredAttribute("value").getValueExpression(faceletContext, String.class);

        StorageGetItemsComponent storageGetItemsComponent = (StorageGetItemsComponent) parent;
        storageGetItemsComponent.addStorageItem(name, type, valueExpression);
    }
}
