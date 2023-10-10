/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.webauthn;

import java.io.Serializable;
import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.AjaxBehaviorListener;

public class MethodExpressionAjaxBehaviorListener implements AjaxBehaviorListener, Serializable {

    private MethodExpression methodExpression;

    public MethodExpressionAjaxBehaviorListener() {
        super();
    }

    public MethodExpressionAjaxBehaviorListener(MethodExpression methodExpression) {
        this.methodExpression = methodExpression;
    }

    @Override
    public void processAjaxBehavior(AjaxBehaviorEvent event) throws AbortProcessingException {
        FacesContext facesContext = event.getFacesContext();
        ELContext elContext = facesContext.getELContext();
        this.methodExpression.invoke(elContext, new Object[]{event});
    }
}
