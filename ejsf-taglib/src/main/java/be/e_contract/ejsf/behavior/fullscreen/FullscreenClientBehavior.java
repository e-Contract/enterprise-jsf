/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.behavior.fullscreen;

import javax.faces.component.behavior.ClientBehaviorBase;
import javax.faces.component.behavior.FacesBehavior;

@FacesBehavior(FullscreenClientBehavior.BEHAVIOR_ID)
public class FullscreenClientBehavior extends ClientBehaviorBase {

    public static final String BEHAVIOR_ID = "ejsf.fullscreenClientBehavior";

    private String component;

    private String onfullscreen;

    public String getComponent() {
        return this.component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getOnfullscreen() {
        return this.onfullscreen;
    }

    public void setOnfullscreen(String onfullscreen) {
        this.onfullscreen = onfullscreen;
    }

    @Override
    public String getRendererType() {
        return FullscreenClientBehaviorRenderer.RENDERER_TYPE;
    }
}
