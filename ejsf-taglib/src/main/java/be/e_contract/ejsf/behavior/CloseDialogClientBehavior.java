/*
 * Enterprise JSF project.
 *
 * Copyright 2021-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.behavior;

import javax.faces.component.behavior.ClientBehaviorBase;
import javax.faces.component.behavior.FacesBehavior;

@FacesBehavior(CloseDialogClientBehavior.BEHAVIOR_ID)
public class CloseDialogClientBehavior extends ClientBehaviorBase {

    public static final String BEHAVIOR_ID = "ejsf.closeDialog";

    @Override
    public String getRendererType() {
        return CloseDialogClientBehaviorRenderer.RENDERER_TYPE;
    }
}
