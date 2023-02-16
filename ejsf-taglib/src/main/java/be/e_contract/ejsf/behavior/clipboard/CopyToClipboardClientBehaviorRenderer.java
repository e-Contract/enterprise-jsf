/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.behavior.clipboard;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.context.FacesContext;
import javax.faces.render.ClientBehaviorRenderer;
import javax.faces.render.FacesBehaviorRenderer;

@FacesBehaviorRenderer(rendererType = CopyToClipboardClientBehaviorRenderer.RENDERER_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "ejsf", name = "utils.js", target = "head")
})
public class CopyToClipboardClientBehaviorRenderer extends ClientBehaviorRenderer {

    public static final String RENDERER_TYPE = "ejsf.copyToClipboardClientBehaviorRenderer";

    @Override
    public String getScript(ClientBehaviorContext behaviorContext, ClientBehavior clientBehavior) {
        CopyToClipboardClientBehavior copyToClipboardClientBehavior = (CopyToClipboardClientBehavior) clientBehavior;
        ValueExpression valueExpression = copyToClipboardClientBehavior.getValueExpression();
        FacesContext facesContext = behaviorContext.getFacesContext();
        ELContext elContext = facesContext.getELContext();
        String value = (String) valueExpression.getValue(elContext);
        return "ejsf.copyToClipboard('" + value + "');";
    }
}
