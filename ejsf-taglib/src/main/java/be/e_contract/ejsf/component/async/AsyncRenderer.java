/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.async;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesRenderer(componentFamily = AsyncComponent.COMPONENT_FAMILY, rendererType = AsyncRenderer.RENDERER_TYPE)
public class AsyncRenderer extends Renderer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncRenderer.class);

    public static final String RENDERER_TYPE = "ejsf.asyncRenderer";

    @Override
    public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
        AsyncComponent asyncComponent = (AsyncComponent) component;
        String clientId = asyncComponent.getClientId();
        ResponseWriter responseWriter = facesContext.getResponseWriter();

        responseWriter.startElement("span", component);
        responseWriter.writeAttribute("id", clientId, "id");
        responseWriter.writeAttribute("data-ejsf-async", "", null);

        Object evaluatedValue = asyncComponent.getEvaluatedValue();
        if (null == evaluatedValue) {
            return;
        }
        List<UIComponent> children = asyncComponent.getChildren();
        for (UIComponent child : children) {
            if (child instanceof UIOutput) {
                UIOutput output = (UIOutput) child;
                output.setValue(evaluatedValue);
                return;
            }
        }
        responseWriter.write(evaluatedValue.toString());
    }

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        ResponseWriter responseWriter = facesContext.getResponseWriter();
        responseWriter.endElement("span");
    }

    @Override
    public void decode(FacesContext facesContext, UIComponent component) {
        ExternalContext externalContext = facesContext.getExternalContext();
        Map<String, String> params = externalContext.getRequestParameterMap();
        String behaviorSource = params.get(ClientBehaviorContext.BEHAVIOR_SOURCE_PARAM_NAME);
        if (null == behaviorSource) {
            return;
        }
        String clientId = component.getClientId(facesContext);
        if (!behaviorSource.equals(clientId)) {
            return;
        }
        String behaviorEvent = params.get(ClientBehaviorContext.BEHAVIOR_EVENT_PARAM_NAME);
        if (null == behaviorEvent) {
            return;
        }
        if ("loadData".equals(behaviorEvent)) {
            AsyncComponent asyncComponent = (AsyncComponent) component;
            String value = asyncComponent.getValue();
            LOGGER.debug("load data via value expression: {}", value);
            ELContext elContext = facesContext.getELContext();
            Application application = facesContext.getApplication();
            ExpressionFactory expressionFactory = application.getExpressionFactory();
            ValueExpression valueExpression = expressionFactory.createValueExpression(elContext, value, Object.class);
            Object evaluatedValue = valueExpression.getValue(elContext);

            PartialViewContext partialViewContext = facesContext.getPartialViewContext();
            String _for = asyncComponent.getFor();
            if (null != _for) {
                UIComponent forComponent = asyncComponent.findComponent(_for);
                if (null != forComponent) {
                    if (forComponent instanceof UIOutput) {
                        UIOutput uiOutput = (UIOutput) forComponent;
                        uiOutput.setValue(evaluatedValue);
                        String forComponentClientId = forComponent.getClientId(facesContext);
                        partialViewContext.getRenderIds().add(forComponentClientId);
                    } else {
                        LOGGER.error("component {} is no UIOutput", _for);
                    }
                } else {
                    LOGGER.error("component not found: {}", _for);
                }
            } else {
                asyncComponent.setEvaluatedValue(evaluatedValue);
                partialViewContext.getRenderIds().add(clientId);
            }
        }
    }
}
