/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.behavior.fullscreen;

import java.io.IOException;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.BehaviorConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagException;
import javax.faces.view.facelets.TagHandler;

public class FullscreenTagHandler extends TagHandler {

    private final TagAttribute componentTagAttribute;

    public FullscreenTagHandler(BehaviorConfig config) {
        super(config);
        this.componentTagAttribute = getRequiredAttribute("component");
    }

    @Override
    public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException {
        if (!ComponentHandler.isNew(parent)) {
            return;
        }
        if (!(parent instanceof ClientBehaviorHolder)) {
            throw new TagException(this.tag, "parent must be ClientBehaviorHolder.");
        }
        ClientBehaviorHolder clientBehaviorHolder = (ClientBehaviorHolder) parent;
        String component = this.componentTagAttribute.getValue();
        FacesContext facesContext = faceletContext.getFacesContext();
        Application application = facesContext.getApplication();
        FullscreenClientBehavior fullscreenClientBehavior
                = (FullscreenClientBehavior) application.createBehavior(FullscreenClientBehavior.BEHAVIOR_ID);
        fullscreenClientBehavior.setComponent(component);
        clientBehaviorHolder.addClientBehavior("click", fullscreenClientBehavior);
    }
}
