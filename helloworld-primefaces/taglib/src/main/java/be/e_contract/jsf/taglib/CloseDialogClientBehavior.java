package be.e_contract.jsf.taglib;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehaviorBase;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.component.behavior.FacesBehavior;
import org.primefaces.component.dialog.Dialog;

@FacesBehavior("ejsf.closeDialog")
public class CloseDialogClientBehavior extends ClientBehaviorBase {

    @Override
    public String getScript(ClientBehaviorContext behaviorContext) {
        UIComponent component = behaviorContext.getComponent();
        while (!Dialog.class.isInstance(component)) {
            component = component.getParent();
            if (null == component) {
                return null;
            }
        }
        Dialog dialog = (Dialog) component;
        String dialogWidgetVar = dialog.resolveWidgetVar();
        return "PF('" + dialogWidgetVar + "').hide()";
    }
}
