/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.behavior.autosubmit;

import javax.faces.component.behavior.ClientBehaviorBase;
import javax.faces.component.behavior.FacesBehavior;

@FacesBehavior(AutoSubmitClientBehavior.BEHAVIOR_ID)
public class AutoSubmitClientBehavior extends ClientBehaviorBase {

    public static final String BEHAVIOR_ID = "ejsf.autoSubmitClientBehavior";

    @Override
    public String getRendererType() {
        return AutoSubmitClientBehaviorRenderer.RENDERER_TYPE;
    }

    private int whenLength;

    public int getWhenLength() {
        return this.whenLength;
    }

    public void setWhenLength(int whenLength) {
        this.whenLength = whenLength;
    }

    private String target;

    public String getTarget() {
        return this.target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
