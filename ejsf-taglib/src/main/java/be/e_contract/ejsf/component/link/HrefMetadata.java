/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.link;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.Metadata;

public class HrefMetadata extends Metadata {

    private final String literalValue;

    public HrefMetadata(String literalValue) {
        this.literalValue = literalValue;
    }

    @Override
    public void applyMetadata(FaceletContext faceletContext, Object instance) {
        LinkComponent linkComponent = (LinkComponent) instance;
        ExpressionFactory expressionFactory = faceletContext.getExpressionFactory();
        ValueExpression hrefValueExpression = expressionFactory.createValueExpression(faceletContext, this.literalValue, String.class);
        linkComponent.setHref(hrefValueExpression);
    }
}
