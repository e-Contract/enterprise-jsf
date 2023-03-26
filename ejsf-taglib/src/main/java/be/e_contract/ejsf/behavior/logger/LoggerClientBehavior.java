/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.behavior.logger;

import javax.faces.component.behavior.ClientBehaviorBase;
import javax.faces.component.behavior.FacesBehavior;

@FacesBehavior(LoggerClientBehavior.BEHAVIOR_ID)
public class LoggerClientBehavior extends ClientBehaviorBase {

    public static final String BEHAVIOR_ID = "ejsf.clientBehaviorLogger";

    private String oneventCallback;

    public String getOneventCallback() {
        return this.oneventCallback;
    }

    public void setOneventCallback(String oneventCallback) {
        this.oneventCallback = oneventCallback;
    }

    @Override
    public String getRendererType() {
        return LoggerClientBehaviorRenderer.RENDERER_TYPE;
    }
}
