/*
 * Enterprise JSF project.
 *
 * Copyright 2023-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.behavior.autosubmit;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.render.ClientBehaviorRenderer;
import javax.faces.render.FacesBehaviorRenderer;
import org.primefaces.expression.SearchExpressionUtils;

@FacesBehaviorRenderer(rendererType = AutoSubmitClientBehaviorRenderer.RENDERER_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "ejsf", name = "utils.js", target = "head")
})
public class AutoSubmitClientBehaviorRenderer extends ClientBehaviorRenderer {

    public static final String RENDERER_TYPE = "ejsf.autoSubmitClientBehaviorRenderer";

    @Override
    public String getScript(ClientBehaviorContext behaviorContext, ClientBehavior clientBehavior) {
        AutoSubmitClientBehavior autoSubmitClientBehavior = (AutoSubmitClientBehavior) clientBehavior;
        int whenLength = autoSubmitClientBehavior.getWhenLength();
        String target = autoSubmitClientBehavior.getTarget();
        if (null != target) {
            UIComponent source = behaviorContext.getComponent();
            UIComponent targetComponent = SearchExpressionUtils.resolveComponent(target, source);
            String targetClientId = targetComponent.getClientId();
            return "ejsf.autoSubmit(event, " + whenLength + ",'" + targetClientId + "')";
        } else {
            return "ejsf.autoSubmit(event, " + whenLength + ")";
        }

    }
}
