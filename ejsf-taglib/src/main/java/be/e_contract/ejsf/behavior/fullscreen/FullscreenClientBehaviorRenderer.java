/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.behavior.fullscreen;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.context.FacesContext;
import javax.faces.render.ClientBehaviorRenderer;
import javax.faces.render.FacesBehaviorRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesBehaviorRenderer(rendererType = FullscreenClientBehaviorRenderer.RENDERER_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "ejsf", name = "fullscreen.js", target = "head")
})
public class FullscreenClientBehaviorRenderer extends ClientBehaviorRenderer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FullscreenClientBehaviorRenderer.class);

    public static final String RENDERER_TYPE = "ejsf.fullscreenBehaviorRenderer";

    @Override
    public String getScript(ClientBehaviorContext behaviorContext, ClientBehavior clientBehavior) {
        FullscreenClientBehavior fullscreenClientBehavior = (FullscreenClientBehavior) clientBehavior;
        String componentId = fullscreenClientBehavior.getComponent();
        UIComponent behaviorComponent = behaviorContext.getComponent();
        UIComponent fullscreenComponent = behaviorComponent.findComponent(componentId);
        if (null == fullscreenComponent) {
            LOGGER.error("component not found: {}", componentId);
            return null;
        }
        FacesContext facesContext = behaviorContext.getFacesContext();
        String fullscreenComponentClientId = fullscreenComponent.getClientId(facesContext);
        String onfullscreen = fullscreenClientBehavior.getOnfullscreen();
        if (null != onfullscreen) {
            onfullscreen = "function() {" + onfullscreen + "}";
        } else {
            onfullscreen = "null";
        }
        return "ejsf.fullscreen('" + fullscreenComponentClientId + "'," + onfullscreen + "); return false;";
    }
}
