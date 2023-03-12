/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.jmsinfo;

import java.io.IOException;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class JmsInfoRenderer extends CoreRenderer {

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        String clientId = component.getClientId();

        JmsInfoComponent jmsInfoComponent = (JmsInfoComponent) component;
        jmsInfoComponent.loadData();

        Application application = context.getApplication();
        ExpressionFactory expressionFactory = application.getExpressionFactory();
        ELContext elContext = context.getELContext();

        ValueExpression replayOncompleteValueExpression = expressionFactory.createValueExpression(elContext,
                "PrimeFaces.getWidgetById('" + clientId + "').messageReplayed('#{jmsMessage.id}')", String.class);
        CommandButton replayButton = jmsInfoComponent.getReplayButton();
        replayButton.setValueExpression("oncomplete", replayOncompleteValueExpression);

        ValueExpression removeOncompleteValueExpression = expressionFactory.createValueExpression(elContext,
                "PrimeFaces.getWidgetById('" + clientId + "').messageRemoved('#{jmsMessage.id}')", String.class);
        CommandButton removeButton = jmsInfoComponent.getRemoveButton();
        removeButton.setValueExpression("oncomplete", removeOncompleteValueExpression);
        super.encodeBegin(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        JmsInfoComponent jmsInfoComponent = (JmsInfoComponent) component;
        WidgetBuilder widgetBuilder = getWidgetBuilder(context);
        widgetBuilder.init("EJSFJmsInfo", jmsInfoComponent);
        encodeClientBehaviors(context, jmsInfoComponent);
        widgetBuilder.finish();
    }

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }
}
