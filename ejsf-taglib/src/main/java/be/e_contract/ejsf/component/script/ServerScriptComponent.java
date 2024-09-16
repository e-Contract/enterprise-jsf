/*
 * Enterprise JSF project.
 *
 * Copyright 2023-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.script;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.PostAddToViewEvent;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(ServerScriptComponent.COMPONENT_TYPE)
public class ServerScriptComponent extends UIComponentBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerScriptComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.serverScriptComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    private static final String SCRIPT_ENGINE_ATTRIBUTE = ServerScriptComponent.class.getName() + ".scriptEngine";

    private static final String FUNCTIONS_ATTRIBUTE = ServerScriptComponent.class.getName() + ".functions";

    private static final String LANGUAGE_CONTEXT_PARAM = "ejsf.serverScript.language";

    private static final String ENABLED_CONTEXT_PARAM = "ejsf.serverScript.enabled";

    public ServerScriptComponent() {
        setRendererType(null);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UIViewRoot viewRoot = facesContext.getViewRoot();
        viewRoot.subscribeToEvent(PostAddToViewEvent.class, this);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
        LOGGER.debug("processEvent: {}", event);
        if (!(event instanceof PostAddToViewEvent)) {
            super.processEvent(event);
            return;
        }
        if (!isEnabled()) {
            LOGGER.warn("serverScript not enabled");
            return;
        }
        String scope = getScope();
        FacesContext facesContext = getFacesContext();
        String functions = getFunctions();
        if (scope.equals("request")) {
            Map<Object, Object> attributes = facesContext.getAttributes();
            if (null != functions) {
                StringTokenizer functionTokens = new StringTokenizer(functions, ",");
                Set<String> functionSet = (Set<String>) attributes.get(FUNCTIONS_ATTRIBUTE);
                if (null == functionSet) {
                    functionSet = new HashSet<>();
                    attributes.put(FUNCTIONS_ATTRIBUTE, functionSet);
                }
                while (functionTokens.hasMoreTokens()) {
                    String function = functionTokens.nextToken();
                    functionSet.add(function);
                }
            }
            ScriptEngine scriptEngine = (ScriptEngine) attributes.get(SCRIPT_ENGINE_ATTRIBUTE);
            if (null == scriptEngine) {
                scriptEngine = createScriptEngine();
                attributes.put(SCRIPT_ENGINE_ATTRIBUTE, scriptEngine);
            }
            applyBinding(facesContext, scriptEngine);
            evalScript(scriptEngine);
        } else if (scope.equals("view")) {
            UIViewRoot viewRoot = facesContext.getViewRoot();
            Map<String, Object> viewMap = viewRoot.getViewMap();
            ScriptEngine scriptEngine = (ScriptEngine) viewMap.get(SCRIPT_ENGINE_ATTRIBUTE);
            if (null == scriptEngine) {
                LOGGER.debug("creating view ScriptEngine");
                scriptEngine = createScriptEngine();
                viewMap.put(SCRIPT_ENGINE_ATTRIBUTE, scriptEngine);
            }
            applyBinding(facesContext, scriptEngine);
            if (!facesContext.isPostback()) {
                if (null != functions) {
                    StringTokenizer functionTokens = new StringTokenizer(functions, ",");
                    Set<String> functionSet = (Set<String>) viewMap.get(FUNCTIONS_ATTRIBUTE);
                    if (null == functionSet) {
                        functionSet = new HashSet<>();
                        viewMap.put(FUNCTIONS_ATTRIBUTE, functionSet);
                    }
                    while (functionTokens.hasMoreTokens()) {
                        String function = functionTokens.nextToken();
                        functionSet.add(function);
                    }
                }
                evalScript(scriptEngine);
            }
        } else {
            LOGGER.error("unsupported scope: {}", scope);
        }
    }

    private boolean isEnabled() {
        FacesContext facesContext = getFacesContext();
        ExternalContext externalContext = facesContext.getExternalContext();
        String enabledInitParam = externalContext.getInitParameter(ENABLED_CONTEXT_PARAM);
        if (UIInput.isEmpty(enabledInitParam)) {
            return false;
        }
        boolean enabled = Boolean.parseBoolean(enabledInitParam);
        return enabled;
    }

    public enum PropertyKeys {
        language,
        scope,
        bind,
        include,
        functions,
    }

    private void evalScript(ScriptEngine scriptEngine) {
        String include = getInclude();
        if (null != include) {
            InputStream includeInputStream = ServerScriptComponent.class.getResourceAsStream(include);
            if (null != includeInputStream) {
                InputStreamReader includeReader = new InputStreamReader(includeInputStream);
                try {
                    scriptEngine.eval(includeReader);
                } catch (ScriptException ex) {
                    LOGGER.error("script error: {} ({},{})", ex.getMessage(), ex.getLineNumber(), ex.getColumnNumber());
                }
            } else {
                LOGGER.error("missing resource: {}", include);
            }
        }
        String script = getScript();
        try {
            scriptEngine.eval(script);
        } catch (ScriptException ex) {
            LOGGER.error("script error: {} ({},{})", ex.getMessage(), ex.getLineNumber(), ex.getColumnNumber());
        }
    }

    private void applyBinding(FacesContext facesContext, ScriptEngine scriptEngine) {
        String bind = getBind();
        if (null == bind) {
            return;
        }
        StringTokenizer bindTokens = new StringTokenizer(bind, ",");
        while (bindTokens.hasMoreTokens()) {
            String bindName = bindTokens.nextToken();
            ELContext elContext = facesContext.getELContext();
            Application application = facesContext.getApplication();
            ExpressionFactory expressionFactory = application.getExpressionFactory();
            ValueExpression valueExpression = expressionFactory.createValueExpression(elContext,
                    "#{" + bindName + "}", Object.class);
            Object value = valueExpression.getValue(elContext);
            LOGGER.debug("bind object: {}", value);
            scriptEngine.put(bindName, value);
        }
    }

    private ScriptEngine createScriptEngine() throws AbortProcessingException {
        String language = getLanguage();
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        if (null != language) {
            ScriptEngine scriptEngine = scriptEngineManager.getEngineByName(language);
            if (null == scriptEngine) {
                LOGGER.error("unsupported script language: {}", language);
                throw new AbortProcessingException("unsupported script language: " + language);
            }
            return scriptEngine;
        } else {
            List<ScriptEngineFactory> scriptEngineFactories = scriptEngineManager.getEngineFactories();
            if (scriptEngineFactories.size() == 1) {
                ScriptEngineFactory scriptEngineFactory = scriptEngineFactories.get(0);
                ScriptEngine scriptEngine = scriptEngineFactory.getScriptEngine();
                return scriptEngine;
            } else {
                FacesContext facesContext = getFacesContext();
                ExternalContext externalContext = facesContext.getExternalContext();
                String defaultLanguage = externalContext.getInitParameter(LANGUAGE_CONTEXT_PARAM);
                if (!UIInput.isEmpty(defaultLanguage)) {
                    LOGGER.debug("using default language: {}", defaultLanguage);
                    ScriptEngine scriptEngine = scriptEngineManager.getEngineByName(defaultLanguage);
                    if (null == scriptEngine) {
                        LOGGER.error("unsupported script language: {}", defaultLanguage);
                        throw new AbortProcessingException("unsupported script language: " + defaultLanguage);
                    }
                    return scriptEngine;
                } else {
                    ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("javascript");
                    if (null == scriptEngine) {
                        LOGGER.error("unsupported script language: {}", "javascript");
                        throw new AbortProcessingException("unsupported script language: " + "javascript");
                    }
                    return scriptEngine;
                }
            }
        }
    }

    public String getLanguage() {
        return (String) getStateHelper().get(PropertyKeys.language);
    }

    public void setLanguage(String language) {
        getStateHelper().put(PropertyKeys.language, language);
    }

    public String getScope() {
        return (String) getStateHelper().eval(PropertyKeys.scope, "request");
    }

    public void setScope(String scope) {
        getStateHelper().put(PropertyKeys.scope, scope);
    }

    public String getBind() {
        return (String) getStateHelper().get(PropertyKeys.bind);
    }

    public void setBind(String bind) {
        getStateHelper().put(PropertyKeys.bind, bind);
    }

    public String getInclude() {
        return (String) getStateHelper().get(PropertyKeys.include);
    }

    public void setInclude(String include) {
        getStateHelper().put(PropertyKeys.include, include);
    }

    public String getFunctions() {
        return (String) getStateHelper().get(PropertyKeys.functions);
    }

    public void setFunctions(String functions) {
        getStateHelper().put(PropertyKeys.functions, functions);
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(FacesContext context) throws IOException {
        if (!isEnabled()) {
            ResponseWriter responseWriter = context.getResponseWriter();
            String clientId = this.getClientId();
            responseWriter.startElement("div", this);
            responseWriter.writeAttribute("id", clientId, "id");
            responseWriter.writeAttribute("style", "color: red;", null);
            responseWriter.writeText("ejsf:serverScript not enabled.", null);
            responseWriter.endElement("div");
        }
    }

    public String getScript() {
        List<UIComponent> children = this.getChildren();
        if (children.isEmpty()) {
            return "";
        }
        UIComponent child = children.get(0);
        String script = child.toString();
        return script;
    }

    public static ScriptEngine findScriptEngine() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map<Object, Object> attributes = facesContext.getAttributes();
        ScriptEngine scriptEngine = (ScriptEngine) attributes.get(SCRIPT_ENGINE_ATTRIBUTE);
        return scriptEngine;
    }

    public static ScriptEngine findViewScriptEngine() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UIViewRoot viewRoot = facesContext.getViewRoot();
        Map<String, Object> viewMap = viewRoot.getViewMap();
        ScriptEngine scriptEngine = (ScriptEngine) viewMap.get(SCRIPT_ENGINE_ATTRIBUTE);
        return scriptEngine;
    }

    public static Set<String> getScriptFunctions() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map<Object, Object> attributes = facesContext.getAttributes();
        Set<String> functions = (Set<String>) attributes.get(FUNCTIONS_ATTRIBUTE);
        if (null == functions) {
            return Collections.EMPTY_SET;
        }
        return functions;
    }

    public static Set<String> getViewScriptFunctions() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UIViewRoot viewRoot = facesContext.getViewRoot();
        Map<String, Object> viewMap = viewRoot.getViewMap();
        Set<String> functions = (Set<String>) viewMap.get(FUNCTIONS_ATTRIBUTE);
        if (null == functions) {
            return Collections.EMPTY_SET;
        }
        return functions;
    }
}
