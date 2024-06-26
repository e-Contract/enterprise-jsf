/*
 * Enterprise JSF project.
 *
 * Copyright 2023-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.behavior.opendialog;

import java.io.IOException;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
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
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.component.commandlink.CommandLink;
import org.primefaces.component.menuitem.UIMenuItem;

public class OpenDialogTagHandler extends TagHandler {

    public OpenDialogTagHandler(BehaviorConfig config) {
        super(config);
    }

    @Override
    public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException {
        if (!ComponentHandler.isNew(parent)) {
            return;
        }

        String dialog;
        TagAttribute dialogIdTagAttribute = getAttribute("dialogId");
        if (null != dialogIdTagAttribute) {
            String dialogId = dialogIdTagAttribute.getValue();
            dialog = "#{p:resolveWidgetVar('" + dialogId + "',component)}";
        } else {
            TagAttribute dialogTagAttribute = getRequiredAttribute("dialog");
            dialog = dialogTagAttribute.getValue();
        }

        String oncomplete;
        TagAttribute oncompleteTagAttribute = getAttribute("oncomplete");
        if (null != oncompleteTagAttribute) {
            oncomplete = "function(){" + oncompleteTagAttribute.getValue() + "}";
        } else {
            oncomplete = "null";
        }

        String whenCallbackParam;
        String oncompleteScript;
        TagAttribute whenCallbackParamTagAttribute = getAttribute("whenCallbackParam");
        if (null != whenCallbackParamTagAttribute) {
            whenCallbackParam = whenCallbackParamTagAttribute.getValue();
            String whenCallbackParamValue;
            TagAttribute whenCallbackParamValueTagAttribute = getAttribute("whenCallbackParamValue");
            if (null != whenCallbackParamValueTagAttribute) {
                whenCallbackParamValue = "'" + whenCallbackParamValueTagAttribute.getValue() + "'";
            } else {
                whenCallbackParamValue = "null";
            }
            oncompleteScript = "ejsf.openDialog('" + dialog + "',status,xhr.pfArgs,'" + whenCallbackParam + "',"
                    + whenCallbackParamValue + "," + oncomplete + ")";
        } else {
            whenCallbackParam = null;
            oncompleteScript = "ejsf.openDialog('" + dialog + "',status,xhr.pfArgs,null,null," + oncomplete + ")";
        }

        FacesContext facesContext = faceletContext.getFacesContext();
        Application application = facesContext.getApplication();

        ValueExpression oncompleteValueExpression;
        if (dialog.startsWith("#{")) {
            ExpressionFactory expressionFactory = application.getExpressionFactory();
            ELContext elContext = facesContext.getELContext();
            oncompleteValueExpression = expressionFactory.createValueExpression(elContext,
                    oncompleteScript, String.class);
        } else {
            oncompleteValueExpression = null;
        }

        boolean configured = false;
        if (parent instanceof CommandButton) {
            CommandButton commandButton = (CommandButton) parent;
            if (null != oncompleteValueExpression) {
                commandButton.setValueExpression("oncomplete", oncompleteValueExpression);
            } else {
                commandButton.setOncomplete(oncompleteScript);
            }
            configured = true;
        }

        if (parent instanceof CommandLink) {
            CommandLink commandLink = (CommandLink) parent;
            if (null != oncompleteValueExpression) {
                commandLink.setValueExpression("oncomplete", oncompleteValueExpression);
            } else {
                commandLink.setOncomplete(oncompleteScript);
            }
            configured = true;
        }

        if (parent instanceof UIMenuItem) {
            UIMenuItem menuItem = (UIMenuItem) parent;
            menuItem.setOncomplete(oncompleteScript);
            configured = true;
        }

        if (!(parent instanceof ClientBehaviorHolder)) {
            throw new TagException(this.tag, "parent must be ClientBehaviorHolder.");
        }

        if (whenCallbackParam != null && !configured) {
            throw new TagException(this.tag, "cannot provide whenCallbackParam on a plain ClientBehaviorHolder.");
        }

        ClientBehaviorHolder clientBehaviorHolder = (ClientBehaviorHolder) parent;
        OpenDialogClientBehavior openDialogClientBehavior
                = (OpenDialogClientBehavior) application.createBehavior(OpenDialogClientBehavior.BEHAVIOR_ID);
        if (!configured) {
            openDialogClientBehavior.setDialog(dialog);
        }
        // we render client behavior anyway, to have our utils.js resource available
        clientBehaviorHolder.addClientBehavior("click", openDialogClientBehavior);
    }
}
