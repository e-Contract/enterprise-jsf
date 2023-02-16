/*
 * Enterprise JSF project.
 *
 * Copyright 2021-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.behavior.closedialog;

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
        UIComponent component = behaviorContext.getComponent();
        String dialogWidgetVar = getDialogWidgetVar(component);
        if (UIInput.isEmpty(dialogWidgetVar)) {
            LOGGER.warn("dialog has no widgetVar - unable to return script to hide() the dialog");
            return null;
        }
        CloseDialogClientBehavior closeDialogClientBehavior = (CloseDialogClientBehavior) clientBehavior;
        String whenCallbackParam = closeDialogClientBehavior.getWhenCallbackParam();
        Boolean whenValid = closeDialogClientBehavior.getWhenValid();
        if (null == whenCallbackParam && null == whenValid) {
            return "ejsf.closeDialog('" + dialogWidgetVar + "')";
        } else {
            return "ejsf.storeDialog(event,'" + dialogWidgetVar + "')";
        }
    }

    private String getDialogWidgetVar(UIComponent component) {
        Optional<Dialog> dialogOptional = findClosestParent(component, Dialog.class);
        if (!dialogOptional.isPresent()) {
            Optional<ConfirmDialog> confirmDialogOptional = findClosestParent(component, ConfirmDialog.class);
            if (!confirmDialogOptional.isPresent()) {
                LOGGER.warn("no parent p:dialog nor p:confirmDialog found - unable to return script to hide() the dialog");
                return null;
            }
            ConfirmDialog confirmDialog = confirmDialogOptional.get();
            return confirmDialog.getWidgetVar();
        } else {
            Dialog dialog = dialogOptional.get();
            return dialog.getWidgetVar();
        }
    }

    private <T extends UIComponent> Optional<T> findClosestParent(UIComponent component, Class<T> parentType) {
        UIComponent parent = component.getParent();
        while (parent != null && !parentType.isInstance(parent)) {
            parent = parent.getParent();
        }
        return Optional.ofNullable(parentType.cast(parent));
    }
}
