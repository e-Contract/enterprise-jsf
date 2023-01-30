/*
 * Enterprise JSF project.
 *
 * Copyright 2021-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.i18n;

import java.util.Optional;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.behavior.ClientBehaviorBase;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.component.behavior.FacesBehavior;
import org.primefaces.component.confirmdialog.ConfirmDialog;
import org.primefaces.component.dialog.Dialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesBehavior("ejsf.closeDialog")
public class CloseDialogClientBehavior extends ClientBehaviorBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloseDialogClientBehavior.class);

    @Override
    public String getScript(ClientBehaviorContext behaviorContext) {
        LOGGER.trace("getScript(..)");
        UIComponent component = behaviorContext.getComponent();
        String dialogWidgetVar;
        Optional<Dialog> dialogOptional = findClosestParent(component, Dialog.class);
        if (!dialogOptional.isPresent()) {
            Optional<ConfirmDialog> confirmDialogOptional = findClosestParent(component, ConfirmDialog.class);
            if (!confirmDialogOptional.isPresent()) {
                LOGGER.warn("no parent p:dialog nor p:confirmDialogfound - unable to return script to hide() the dialog");
                return null;
            }
            ConfirmDialog confirmDialog = confirmDialogOptional.get();
            dialogWidgetVar = confirmDialog.getWidgetVar();
        } else {
            Dialog dialog = dialogOptional.get();
            dialogWidgetVar = dialog.getWidgetVar();
        }
        if (UIInput.isEmpty(dialogWidgetVar)) {
            LOGGER.warn("dialog has no widgetVar - unable to return script to hide() the dialog");
            return null;
        }
        return "PF('" + dialogWidgetVar + "').hide()";
    }

    private <T extends UIComponent> Optional<T> findClosestParent(UIComponent component, Class<T> parentType) {
        UIComponent parent = component.getParent();
        while (parent != null && !parentType.isInstance(parent)) {
            parent = parent.getParent();
        }
        return Optional.ofNullable(parentType.cast(parent));
    }
}
