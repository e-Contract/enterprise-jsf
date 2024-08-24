/*
 * Enterprise JSF project.
 *
 * Copyright 2021-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.behavior.closedialog;

import be.e_contract.ejsf.component.dialog.DialogComponent;
import java.util.Optional;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.render.ClientBehaviorRenderer;
import javax.faces.render.FacesBehaviorRenderer;
import org.primefaces.component.confirmdialog.ConfirmDialog;
import org.primefaces.component.dialog.Dialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesBehaviorRenderer(rendererType = CloseDialogClientBehaviorRenderer.RENDERER_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "ejsf", name = "utils.js", target = "head")
})
public class CloseDialogClientBehaviorRenderer extends ClientBehaviorRenderer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloseDialogClientBehaviorRenderer.class);

    public static final String RENDERER_TYPE = "ejsf.closeDialogRenderer";

    @Override
    public String getScript(ClientBehaviorContext behaviorContext, ClientBehavior clientBehavior) {
        CloseDialogClientBehavior closeDialogClientBehavior = (CloseDialogClientBehavior) clientBehavior;
        String dialogWidgetVar = closeDialogClientBehavior.getTarget();
        if (null == dialogWidgetVar) {
            UIComponent component = behaviorContext.getComponent();
            dialogWidgetVar = getDialogWidgetVar(component);
            if (UIInput.isEmpty(dialogWidgetVar)) {
                LOGGER.warn("dialog has no widgetVar - unable to return script to hide() the dialog");
                return null;
            }
        }

        String whenCallbackParam = closeDialogClientBehavior.getWhenCallbackParam();
        Boolean whenValid = closeDialogClientBehavior.getWhenValid();
        if (null == whenCallbackParam && null == whenValid) {
            boolean noAjax = closeDialogClientBehavior.isNoAjax();
            return "return ejsf.closeDialog('" + dialogWidgetVar + "'," + noAjax + ")";
        } else {
            return "ejsf.storeDialog(event,'" + dialogWidgetVar + "')";
        }
    }

    private String getDialogWidgetVar(UIComponent component) {
        Optional<Dialog> dialogOptional = findClosestParent(component, Dialog.class);
        if (dialogOptional.isPresent()) {
            Dialog dialog = dialogOptional.get();
            return dialog.resolveWidgetVar();
        }
        Optional<ConfirmDialog> confirmDialogOptional = findClosestParent(component, ConfirmDialog.class);
        if (confirmDialogOptional.isPresent()) {
            ConfirmDialog confirmDialog = confirmDialogOptional.get();
            return confirmDialog.resolveWidgetVar();
        }
        Optional<DialogComponent> dialogComponentOptional = findClosestParent(component, DialogComponent.class);
        if (dialogComponentOptional.isPresent()) {
            DialogComponent dialogComponent = dialogComponentOptional.get();
            return dialogComponent.resolveWidgetVar();
        }

        LOGGER.warn("no parent p:dialog nor p:confirmDialog not ejsf:dialog found - unable to return script to hide() the dialog");
        return null;
    }

    private <T extends UIComponent> Optional<T> findClosestParent(UIComponent component, Class<T> parentType) {
        UIComponent parent = component.getParent();
        while (parent != null && !parentType.isInstance(parent)) {
            parent = parent.getParent();
        }
        return Optional.ofNullable(parentType.cast(parent));
    }
}
