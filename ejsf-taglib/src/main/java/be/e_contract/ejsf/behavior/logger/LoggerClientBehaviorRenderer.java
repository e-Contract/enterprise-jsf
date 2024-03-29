/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.behavior.logger;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.render.ClientBehaviorRenderer;
import javax.faces.render.FacesBehaviorRenderer;

@FacesBehaviorRenderer(rendererType = LoggerClientBehaviorRenderer.RENDERER_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "ejsf", name = "utils.js", target = "head")
})
public class LoggerClientBehaviorRenderer extends ClientBehaviorRenderer {

    public static final String RENDERER_TYPE = "ejsf.clientBehaviorLoggerRenderer";

    @Override
    public String getScript(ClientBehaviorContext behaviorContext, ClientBehavior clientBehavior) {
        LoggerClientBehavior loggerClientBehavior = (LoggerClientBehavior) clientBehavior;
        String oneventCallback = loggerClientBehavior.getOneventCallback();
        if (null == oneventCallback) {
            return "ejsf.logClientBehavior(event)";
        } else {
            return "ejsf.logClientBehavior(event, function(event) {" + oneventCallback + "})";
        }
    }
}
