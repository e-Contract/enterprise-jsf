/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.insertfacet;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.event.PostAddToViewEvent;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;

public class InsertFacetTagHandler extends TagHandler {

    private final TagAttribute nameTagAttribute;

    private final TagAttribute targetNameTagAttribute;

    public InsertFacetTagHandler(final TagConfig config) {
        super(config);
        this.nameTagAttribute = getRequiredAttribute("name");
        this.targetNameTagAttribute = getAttribute("targetName");
    }

    @Override
    public void apply(FaceletContext context, UIComponent parent) throws IOException {
        if (!ComponentHandler.isNew(parent)) {
            return;
        }
        String name = this.nameTagAttribute.getValue(context);
        String targetName;
        if (null != this.targetNameTagAttribute) {
            targetName = this.targetNameTagAttribute.getValue(context);
        } else {
            targetName = null;
        }
        parent.subscribeToEvent(PostAddToViewEvent.class, new InsertFacetListener(name, targetName));
    }
}
