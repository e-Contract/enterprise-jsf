/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.behavior.autosubmit;

import java.io.IOException;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.BehaviorConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagException;
import javax.faces.view.facelets.TagHandler;

public class AutoSubmitTagHandler extends TagHandler {

    public AutoSubmitTagHandler(BehaviorConfig config) {
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
        if (!(parent instanceof UIInput)) {
            throw new TagException(this.tag, "parent must be UIInput.");
        }
        TagAttribute onLengthTagAttribute = getRequiredAttribute("onLength");
        int onLength = onLengthTagAttribute.getInt(faceletContext);

        String target;
        TagAttribute targetTagAttribute = getAttribute("target");
        if (null != targetTagAttribute) {
            target = targetTagAttribute.getValue(faceletContext);
        } else {
            target = null;
        }

        ClientBehaviorHolder clientBehaviorHolder = (ClientBehaviorHolder) parent;
        FacesContext facesContext = faceletContext.getFacesContext();
        Application application = facesContext.getApplication();
        AutoSubmitClientBehavior autoSubmitClientBehavior
                = (AutoSubmitClientBehavior) application.createBehavior(AutoSubmitClientBehavior.BEHAVIOR_ID);
        autoSubmitClientBehavior.setOnLength(onLength);
        autoSubmitClientBehavior.setTarget(target);
        clientBehaviorHolder.addClientBehavior("input", autoSubmitClientBehavior);
    }
}
