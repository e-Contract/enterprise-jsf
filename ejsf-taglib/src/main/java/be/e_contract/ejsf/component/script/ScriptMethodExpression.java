/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.script;

import java.util.AbstractMap;
import java.util.Optional;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.MethodInfo;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptMethodExpression extends MethodExpression {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptMethodExpression.class);

    private final String methodName;

    private final ScriptEngine scriptEngine;

    public ScriptMethodExpression(String methodName, ScriptEngine scriptEngine) {
        this.methodName = methodName;
        this.scriptEngine = scriptEngine;
    }

    @Override
    public MethodInfo getMethodInfo(ELContext context) {
        LOGGER.debug("getMethodInfo");
        return null;
    }

    @Override
    public Object invoke(ELContext context, Object[] params) {
        LOGGER.debug("invoke");
        params = addUIDataVariableParameter(params);
        Invocable invocable = (Invocable) this.scriptEngine;
        try {
            Object result = invocable.invokeFunction(this.methodName, params);
            LOGGER.debug("result: {}", result);
            if (null != result) {
                LOGGER.debug("result type: {}", result.getClass().getName());
                if ("bsh.Primitive".equals(result.getClass().getName())) {
                    if (result.toString().equals("void")) {
                        return null;
                    }
                }
            }
            return result;
        } catch (ScriptException ex) {
            LOGGER.error("script error: " + ex.getMessage());
        } catch (NoSuchMethodException ex) {
            LOGGER.error("no such method error: " + ex.getMessage());
        }
        return null;
    }

    @Override
    public String getExpressionString() {
        LOGGER.debug("getExpressionString");
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        LOGGER.debug("equals");
        return false;
    }

    @Override
    public int hashCode() {
        LOGGER.debug("hashCode");
        return 0;
    }

    @Override
    public boolean isLiteralText() {
        LOGGER.debug("isLiteralText");
        return false;
    }

    private Object[] addUIDataVariableParameter(Object[] params) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UIComponent currentComponent = UIComponent.getCurrentComponent(facesContext);
        Optional<UIData> uiDataOptional = findClosestParent(currentComponent, UIData.class);
        if (uiDataOptional.isPresent()) {
            UIData uiDataComponent = uiDataOptional.get();
            String var = uiDataComponent.getVar();
            ELContext elContext = facesContext.getELContext();
            Application application = facesContext.getApplication();
            ExpressionFactory expressionFactory = application.getExpressionFactory();
            ValueExpression valueExpression = expressionFactory.createValueExpression(elContext,
                    "#{" + var + "}", Object.class);
            Object value = valueExpression.getValue(elContext);
            LOGGER.debug("var value: {}", value);
            LOGGER.debug("var value type: {}", value.getClass().getName());
            if (value instanceof AbstractMap.SimpleImmutableEntry) {
                AbstractMap.SimpleImmutableEntry valueEntry = (AbstractMap.SimpleImmutableEntry) value;
                value = valueEntry.getValue();
            }
            if (null == params) {
                params = new Object[1];
                params[0] = value;
                return params;
            } else {
                Object[] newParams = new Object[params.length + 1];
                System.arraycopy(params, 0, newParams, 0, params.length);
                newParams[params.length] = value;
                return newParams;
            }
        } else {
            return params;
        }
    }

    private <T extends UIComponent> Optional<T> findClosestParent(UIComponent component, Class<T> parentType) {
        UIComponent parent = component.getParent();
        while (parent != null && !parentType.isInstance(parent)) {
            parent = parent.getParent();
        }
        return Optional.ofNullable(parentType.cast(parent));
    }
}
