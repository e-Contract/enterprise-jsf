/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.behavior.clipboard;

import javax.el.ValueExpression;
import javax.faces.component.behavior.ClientBehaviorBase;
import javax.faces.component.behavior.FacesBehavior;

@FacesBehavior(CopyToClipboardClientBehavior.BEHAVIOR_ID)
public class CopyToClipboardClientBehavior extends ClientBehaviorBase {

    public static final String BEHAVIOR_ID = "ejsf.copyToClipboardClientBehavior";

    @Override
    public String getRendererType() {
        return CopyToClipboardClientBehaviorRenderer.RENDERER_TYPE;
    }

    private ValueExpression valueExpression;

    public ValueExpression getValueExpression() {
        return this.valueExpression;
    }

    public void setValueExpression(ValueExpression valueExpression) {
        this.valueExpression = valueExpression;
    }
}
