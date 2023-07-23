/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.announcement;

import java.io.IOException;
import javax.el.ValueExpression;
import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagException;
import javax.faces.view.facelets.TagHandler;

public class AcceptAnnouncementTagHandler extends TagHandler {

    private final TagAttribute retentionAttribute;

    public AcceptAnnouncementTagHandler(final TagConfig config) {
        super(config);
        this.retentionAttribute = getRequiredAttribute("retention");
    }

    @Override
    public void apply(FaceletContext context, UIComponent parent) throws IOException {
        if (parent == null) {
            return;
        }
        if (!ComponentHandler.isNew(parent)) {
            return;
        }
        if (!(parent instanceof ActionSource)) {
            throw new TagException(this.tag, "Can only be attached to ActionSource components.");
        }

        ValueExpression retentionValueExpression = this.retentionAttribute.getValueExpression(context, Integer.class);

        ActionSource actionSource = (ActionSource) parent;
        actionSource.addActionListener(new AcceptAnnouncementActionListener(retentionValueExpression));
    }
}
