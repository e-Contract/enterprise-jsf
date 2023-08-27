/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.script;

import java.beans.FeatureDescriptor;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Set;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptELResolver extends ELResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptELResolver.class);

    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        LOGGER.debug("getValue: {} {}", base, property);
        if (base == null) {
            ScriptEngine scriptEngine = ServerScriptComponent.findScriptEngine();
            if (scriptEngine != null) {
                Set<String> functions = ServerScriptComponent.getScriptFunctions();
                LOGGER.debug("script functions: {}", functions);
                if (functions.contains(property)) {
                    LOGGER.debug("script function found: {}", property);
                    context.setPropertyResolved(true);
                    ScriptMethodExpression scriptMethodExpression = new ScriptMethodExpression(property.toString(), scriptEngine);
                    return scriptMethodExpression;
                }
                Bindings engineScopeBindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
                if (engineScopeBindings.containsKey(property)) {
                    context.setPropertyResolved(true);
                    Object value = engineScopeBindings.get(property);
                    LOGGER.debug("getValue: {} {} = \"{}\"", base, property, value);
                    if (null == value) {
                        return null;
                    }
                    LOGGER.debug("value type: {}", value.getClass().getName());
                    LOGGER.debug("property type: {}", property.getClass().getName());
                    if (value instanceof Bindings) {
                        Bindings valueBindings = (Bindings) value;
                        if (!valueBindings.isEmpty()) {
                            // required for method invocations
                            context.putContext(ScriptEngine.class, scriptEngine);
                        }
                    }
                    return value;
                }
            }
            ScriptEngine viewScriptEngine = ServerScriptComponent.findViewScriptEngine();
            if (viewScriptEngine != null) {
                Set<String> viewFunctions = ServerScriptComponent.getViewScriptFunctions();
                LOGGER.debug("view functions: {}", viewFunctions);
                if (viewFunctions.contains(property)) {
                    LOGGER.debug("view script function found: {}", property);
                    context.setPropertyResolved(true);
                    ScriptMethodExpression scriptMethodExpression = new ScriptMethodExpression(property.toString(), viewScriptEngine);
                    return scriptMethodExpression;
                }
                Bindings engineScopeBindings = viewScriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
                if (engineScopeBindings.containsKey(property)) {
                    context.setPropertyResolved(true);
                    Object value = engineScopeBindings.get(property);
                    LOGGER.debug("getValue (view scope): {} {} = \"{}\"", base, property, value);
                    if (null == value) {
                        return null;
                    }
                    LOGGER.debug("value type: {}", value.getClass().getName());
                    if (value instanceof Bindings) {
                        Bindings valueBindings = (Bindings) value;
                        if (!valueBindings.isEmpty()) {
                            context.putContext(ScriptEngine.class, viewScriptEngine);
                        }
                    }
                    return value;
                }
            }
        } else {
            LOGGER.debug("base type: {}", base.getClass().getName());
            if (base instanceof AbstractMap.SimpleImmutableEntry) {
                AbstractMap.SimpleImmutableEntry baseEntry = (AbstractMap.SimpleImmutableEntry) base;
                Object baseEntryValue = baseEntry.getValue();
                LOGGER.debug("base entry value: {}", baseEntryValue);
                if (baseEntryValue instanceof Bindings) {
                    Bindings baseEntryValueBindings = (Bindings) baseEntryValue;
                    LOGGER.debug("base entry value bindings: {}", baseEntryValueBindings.keySet());
                    if (baseEntryValueBindings.containsKey(property)) {
                        context.setPropertyResolved(true);
                        return baseEntryValueBindings.get(property);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Object convertToType(ELContext context, Object obj, Class<?> targetType) {
        LOGGER.debug("convertToType: {} {}", obj, targetType);
        return null;
    }

    @Override
    public Object invoke(ELContext context, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
        LOGGER.debug("invoke: {} {}", base, method);
        if (base instanceof Bindings) {
            LOGGER.debug("script method invocation");
            ScriptEngine scriptEngine = (ScriptEngine) context.getContext(ScriptEngine.class);
            Invocable invocable = (Invocable) scriptEngine;
            context.setPropertyResolved(true);
            try {
                return invocable.invokeMethod(base, method.toString(), params);
            } catch (ScriptException ex) {
                LOGGER.error("script error: " + ex.getMessage());
            } catch (NoSuchMethodException ex) {
                LOGGER.error("no such method error: " + ex.getMessage());
            }
        }
        return null;
    }

    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        LOGGER.debug("getType: {} {}", base, property);
        return null;
    }

    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) {
        LOGGER.debug("setValue: {} {}", base, property);
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        LOGGER.debug("isReadOnly: {}", base, property);
        return true;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        LOGGER.debug("getFeatureDescriptors: {}", base);
        return null;
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        LOGGER.debug("getCommonPropertyType: {}", base);
        return null;
    }
}
