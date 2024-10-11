/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.message;

import java.io.IOException;
import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.event.ActionListener;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagException;
import javax.faces.view.facelets.TagHandler;

public class AddMessageParamTagHandler extends TagHandler {

    public AddMessageParamTagHandler(TagConfig tagConfig) {
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
        ActionListener[] actionListeners = actionSource.getActionListeners();
        if (null == actionListeners) {
            throw new TagException(this.tag, "No action listeners found.");
        }
        if (actionListeners.length == 0) {
            throw new TagException(this.tag, "No action listeners found.");
        }
        ActionListener lastActionListener = actionListeners[actionListeners.length - 1];
        if (!(lastActionListener instanceof AddMessageActionListener)) {
            throw new TagException(this.tag, "Last action listener should be AddMessageActionListener");
        }
        AddMessageActionListener addMessageActionListener = (AddMessageActionListener) lastActionListener;

        TagAttribute valueTagAttribute = getRequiredAttribute("value");
        String value = valueTagAttribute.getValue();

        TagAttribute forTagAttribute = getAttribute("for");
        if (null != forTagAttribute) {
            String _for = forTagAttribute.getValue();
            if ("DETAIL".equals(_for.toUpperCase())) {
                addMessageActionListener.addDetailParam(value);
            } else {
                addMessageActionListener.addSummaryParam(value);
            }
        } else {
            addMessageActionListener.addSummaryParam(value);
        }
    }
}
