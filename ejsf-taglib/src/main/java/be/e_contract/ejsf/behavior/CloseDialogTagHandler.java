/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.behavior;

import java.io.IOException;
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
import org.primefaces.component.commandbutton.CommandButton;

public class CloseDialogTagHandler extends TagHandler {

    public CloseDialogTagHandler(BehaviorConfig config) {
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
        String whenCallbackParam;
        TagAttribute whenCallbackParamTagAttribute = getAttribute("whenCallbackParam");
        if (null != whenCallbackParamTagAttribute) {
            whenCallbackParam = whenCallbackParamTagAttribute.getValue();
        } else {
            whenCallbackParam = null;
        }
        Boolean whenValid;
        TagAttribute whenValidTagAttribute = getAttribute("whenValid");
        if (null != whenValidTagAttribute) {
            whenValid = Boolean.valueOf(whenValidTagAttribute.getValue());
        } else {
            whenValid = null;
        }
        if (null != whenCallbackParam || null != whenValid) {
            CommandButton commandButton = (CommandButton) parent;
            commandButton.setOncomplete("ejsf.handleDialogOnComplete(event, status, xhr.pfArgs, '"
                    + whenCallbackParam + "','" + whenValid + "')");
        }
        ClientBehaviorHolder clientBehaviorHolder = (ClientBehaviorHolder) parent;
        FacesContext facesContext = faceletContext.getFacesContext();
        Application application = facesContext.getApplication();
        CloseDialogClientBehavior closeDialogClientBehavior
                = (CloseDialogClientBehavior) application.createBehavior(CloseDialogClientBehavior.BEHAVIOR_ID);
        closeDialogClientBehavior.setWhenCallbackParam(whenCallbackParam);
        closeDialogClientBehavior.setWhenValid(whenValid);
        clientBehaviorHolder.addClientBehavior("click", closeDialogClientBehavior);
    }
}
