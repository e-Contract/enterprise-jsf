/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.behavior.clipboard;

import java.io.IOException;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.BehaviorConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagException;
import javax.faces.view.facelets.TagHandler;

public class CopyToClipboardTagHandler extends TagHandler {

    public CopyToClipboardTagHandler(BehaviorConfig config) {
        super(config);
    }

    @Override
    public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException {
        if (!ComponentHandler.isNew(parent)) {
            return;
        }
        if (!(parent instanceof ClientBehaviorHolder)) {
            throw new TagException(this.tag, "parent must be ClientBehaviorHolder.");
        }
        TagAttribute valueTagAttribute = getRequiredAttribute("value");
        ValueExpression valueExpression = valueTagAttribute.getValueExpression(faceletContext, String.class);

        ClientBehaviorHolder clientBehaviorHolder = (ClientBehaviorHolder) parent;
        FacesContext facesContext = faceletContext.getFacesContext();
        Application application = facesContext.getApplication();
        CopyToClipboardClientBehavior copyToClipboardClientBehavior
                = (CopyToClipboardClientBehavior) application.createBehavior(CopyToClipboardClientBehavior.BEHAVIOR_ID);
        copyToClipboardClientBehavior.setValueExpression(valueExpression);
        clientBehaviorHolder.addClientBehavior("click", copyToClipboardClientBehavior);
    }
}
