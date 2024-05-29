/*
 * Enterprise JSF project.
 *
 * Copyright 2023-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.behavior.closedialog;

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

        String whenCallbackParamValue;
        TagAttribute whenCallbackParamValueTagAttribute = getAttribute("whenCallbackParamValue");
        if (null != whenCallbackParamValueTagAttribute) {
            whenCallbackParamValue = whenCallbackParamValueTagAttribute.getValue();
        } else {
            whenCallbackParamValue = null;
        }
        if (null != whenCallbackParamValue && whenCallbackParam == null) {
            throw new TagException(this.tag, "missing whenCallbackParam");
        }

        Boolean whenValid;
        TagAttribute whenValidTagAttribute = getAttribute("whenValid");
        if (null != whenValidTagAttribute) {
            whenValid = Boolean.valueOf(whenValidTagAttribute.getValue());
        } else {
            whenValid = null;
        }

        String target;
        TagAttribute targetTagAttribute = getAttribute("target");
        if (null != targetTagAttribute) {
            target = targetTagAttribute.getValue();
        } else {
            target = null;
        }

        boolean noAjax = false;
        TagAttribute noAjaxTagAttribute = getAttribute("noAjax");
        if (null != noAjaxTagAttribute) {
            noAjax = noAjaxTagAttribute.getBoolean(faceletContext);
        }

        if (null != whenCallbackParam || null != whenValid) {
            String onCompleteScript = "ejsf.handleDialogOnComplete(event, status, xhr.pfArgs, '"
                    + whenCallbackParam + "','" + whenCallbackParamValue + "','" + whenValid + "')";
            if (parent instanceof CommandButton) {
                CommandButton commandButton = (CommandButton) parent;
                commandButton.setOncomplete(onCompleteScript);
            } else if (parent instanceof CommandLink) {
                CommandLink commandLink = (CommandLink) parent;
                commandLink.setOncomplete(onCompleteScript);
            } else {
                throw new TagException(this.tag, "unsupported parent: " + parent.getClass().getName());
            }
        }
        ClientBehaviorHolder clientBehaviorHolder = (ClientBehaviorHolder) parent;
        FacesContext facesContext = faceletContext.getFacesContext();
        Application application = facesContext.getApplication();
        CloseDialogClientBehavior closeDialogClientBehavior
                = (CloseDialogClientBehavior) application.createBehavior(CloseDialogClientBehavior.BEHAVIOR_ID);
        closeDialogClientBehavior.setWhenCallbackParam(whenCallbackParam);
        closeDialogClientBehavior.setWhenValid(whenValid);
        closeDialogClientBehavior.setTarget(target);
        closeDialogClientBehavior.setNoAjax(noAjax);
        clientBehaviorHolder.addClientBehavior("click", closeDialogClientBehavior);
    }
}
