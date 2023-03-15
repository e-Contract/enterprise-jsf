/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.behavior.opendialog;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.context.FacesContext;
import javax.faces.render.ClientBehaviorRenderer;
import javax.faces.render.FacesBehaviorRenderer;

@FacesBehaviorRenderer(rendererType = OpenDialogClientBehaviorRenderer.RENDERER_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "ejsf", name = "utils.js", target = "head")
})
public class OpenDialogClientBehaviorRenderer extends ClientBehaviorRenderer {

    public static final String RENDERER_TYPE = "ejsf.openDialogRenderer";

    @Override
    public String getScript(ClientBehaviorContext behaviorContext, ClientBehavior clientBehavior) {
        OpenDialogClientBehavior openDialogClientBehavior = (OpenDialogClientBehavior) clientBehavior;
        String dialog = openDialogClientBehavior.getDialog();
        if (null == dialog) {
            return null;
        }
        if (dialog.startsWith("#{")) {
            FacesContext facesContext = behaviorContext.getFacesContext();
            Application application = facesContext.getApplication();
            ExpressionFactory expressionFactory = application.getExpressionFactory();
            ELContext elContext = facesContext.getELContext();
            ValueExpression dialogValueExpression = expressionFactory.createValueExpression(elContext,
                    dialog, String.class);
            dialog = (String) dialogValueExpression.getValue(elContext);
        }
        return "ejsf.openDialog('" + dialog + "')";
    }
}
