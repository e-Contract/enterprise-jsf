/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.behavior.opendialog;

import javax.faces.component.behavior.ClientBehaviorBase;
import javax.faces.component.behavior.FacesBehavior;

@FacesBehavior(OpenDialogClientBehavior.BEHAVIOR_ID)
public class OpenDialogClientBehavior extends ClientBehaviorBase {

    public static final String BEHAVIOR_ID = "ejsf.openDialog";

    private String dialog;

    public String getDialog() {
        return this.dialog;
    }

    public void setDialog(String dialog) {
        this.dialog = dialog;
    }

    @Override
    public String getRendererType() {
        return OpenDialogClientBehaviorRenderer.RENDERER_TYPE;
    }
}
