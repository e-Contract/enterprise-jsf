/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.message;

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

public class AddMessageTagHandler extends TagHandler {

    public AddMessageTagHandler(TagConfig tagConfig) {
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

        TagAttribute severityTagAttribute = getAttribute("severity");
        String severity;
        if (null != severityTagAttribute) {
            severity = severityTagAttribute.getValue();
        } else {
            severity = null;
        }

        TagAttribute summaryTagAttribute = getAttribute("summary");
        ValueExpression summary;
        if (null != summaryTagAttribute) {
            summary = summaryTagAttribute.getValueExpression(faceletContext, String.class);
        } else {
            summary = null;
        }

        TagAttribute detailTagAttribute = getAttribute("detail");
        ValueExpression detail;
        if (null != detailTagAttribute) {
            detail = detailTagAttribute.getValueExpression(faceletContext, String.class);
        } else {
            detail = null;
        }

        TagAttribute targetTagAttribute = getAttribute("target");
        String target;
        if (null != targetTagAttribute) {
            target = targetTagAttribute.getValue();
        } else {
            target = null;
        }

        String whenCallbackParam;
        TagAttribute whenCallbackParamTagAttribute = getAttribute("whenCallbackParam");
        if (null != whenCallbackParamTagAttribute) {
            whenCallbackParam = whenCallbackParamTagAttribute.getValue();
        } else {
            whenCallbackParam = null;
        }

        String whenCallbackParamValue;
        TagAttribute whenCallbackParamValueTagAttribute = getAttribute("whenCallbackParamValue");
        if (null != whenCallbackParamValueTagAttribute) {
            whenCallbackParamValue = whenCallbackParamValueTagAttribute.getValue();
        } else {
            whenCallbackParamValue = null;
        }

        AddMessageActionListener actionListener = new AddMessageActionListener(severity, summary, detail,
                target, whenCallbackParam, whenCallbackParamValue);
        actionSource.addActionListener(actionListener);
    }
}
