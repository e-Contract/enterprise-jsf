/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.input.template;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.Metadata;

public class ResultMetadata extends Metadata {

    private final String literalValue;

    public ResultMetadata(String literalValue) {
        this.literalValue = literalValue;
    }

    @Override
    public void applyMetadata(FaceletContext faceletContext, Object instance) {
        InputTemplateComponent inputTemplateComponent = (InputTemplateComponent) instance;
        ExpressionFactory expressionFactory = faceletContext.getExpressionFactory();
        ValueExpression valueExpression = expressionFactory.createValueExpression(faceletContext, this.literalValue, String.class);
        inputTemplateComponent.setResult(valueExpression);
    }
}
