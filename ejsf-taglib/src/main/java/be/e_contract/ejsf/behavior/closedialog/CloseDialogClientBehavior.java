/*
 * Enterprise JSF project.
 *
 * Copyright 2021-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.behavior.closedialog;

import javax.faces.component.behavior.ClientBehaviorBase;
import javax.faces.component.behavior.FacesBehavior;

@FacesBehavior(CloseDialogClientBehavior.BEHAVIOR_ID)
public class CloseDialogClientBehavior extends ClientBehaviorBase {

    public static final String BEHAVIOR_ID = "ejsf.closeDialog";

    private String whenCallbackParam;
    private Boolean whenValid;
    private String target;
    private boolean noAjax;

    public String getWhenCallbackParam() {
        return this.whenCallbackParam;
    }

    public void setWhenCallbackParam(String whenCallbackParam) {
        this.whenCallbackParam = whenCallbackParam;
    }

    public Boolean getWhenValid() {
        return this.whenValid;
    }

    public void setWhenValid(Boolean whenValid) {
        this.whenValid = whenValid;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTarget() {
        return this.target;
    }

    public void setNoAjax(boolean noAjax) {
        this.noAjax = noAjax;
    }

    public boolean isNoAjax() {
        return this.noAjax;
    }

    @Override
    public String getRendererType() {
        return CloseDialogClientBehaviorRenderer.RENDERER_TYPE;
    }
}
