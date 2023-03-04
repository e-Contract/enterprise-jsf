/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.behavior.opendialog;

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
import org.primefaces.component.commandlink.CommandLink;

public class OpenDialogTagHandler extends TagHandler {

    public OpenDialogTagHandler(BehaviorConfig config) {
        super(config);
    }

    @Override
    public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException {
        if (!ComponentHandler.isNew(parent)) {
            return;
        }

        TagAttribute dialogTagAttribute = getRequiredAttribute("dialog");
        String dialog = dialogTagAttribute.getValue();

        String whenCallbackParam;
        String oncompleteScript;
        TagAttribute whenCallbackParamTagAttribute = getAttribute("whenCallbackParam");
        if (null != whenCallbackParamTagAttribute) {
            whenCallbackParam = whenCallbackParamTagAttribute.getValue();
            String whenCallbackParamValue;
            TagAttribute whenCallbackParamValueTagAttribute = getAttribute("whenCallbackParamValue");
            if (null != whenCallbackParamValueTagAttribute) {
                whenCallbackParamValue = whenCallbackParamValueTagAttribute.getValue();
            } else {
                whenCallbackParamValue = null;
            }
            oncompleteScript = "ejsf.openDialog('" + dialog + "',status,xhr.pfArgs,'" + whenCallbackParam + "','" + whenCallbackParamValue + "')";
        } else {
            whenCallbackParam = null;
            oncompleteScript = "ejsf.openDialog('" + dialog + "',status,xhr.pfArgs)";
        }

        boolean configured = false;
        if (parent instanceof CommandButton) {
            CommandButton commandButton = (CommandButton) parent;
            commandButton.setOncomplete(oncompleteScript);
            configured = true;
        }

        if (parent instanceof CommandLink) {
            CommandLink commandLink = (CommandLink) parent;
            commandLink.setOncomplete(oncompleteScript);
            configured = true;
        }

        if (!(parent instanceof ClientBehaviorHolder)) {
            throw new TagException(this.tag, "parent must be ClientBehaviorHolder.");
        }

        if (whenCallbackParam != null && !configured) {
            throw new TagException(this.tag, "cannot provide whenCallbackParam on a plain ClientBehaviorHolder.");
        }

        ClientBehaviorHolder clientBehaviorHolder = (ClientBehaviorHolder) parent;
        FacesContext facesContext = faceletContext.getFacesContext();
        Application application = facesContext.getApplication();
        OpenDialogClientBehavior openDialogClientBehavior
                = (OpenDialogClientBehavior) application.createBehavior(OpenDialogClientBehavior.BEHAVIOR_ID);
        if (!configured) {
            openDialogClientBehavior.setDialog(dialog);
        }
        // we render client behavior anyway, to have our utils.js resource available
        clientBehaviorHolder.addClientBehavior("click", openDialogClientBehavior);
    }
}
