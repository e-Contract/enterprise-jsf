/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.unit.be.e_contract.ejsf;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptingTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptingTest.class);

    @Test
    public void testScripting() throws Exception {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        List<ScriptEngineFactory> scriptEngineFactories = scriptEngineManager.getEngineFactories();
        for (ScriptEngineFactory scriptEngineFactory : scriptEngineFactories) {
            LOGGER.debug("script engine name: {}", scriptEngineFactory.getEngineName());
            LOGGER.debug("script engine version: {}", scriptEngineFactory.getEngineVersion());
            LOGGER.debug("script engine language: {}", scriptEngineFactory.getLanguageName());
            LOGGER.debug("script engine language version: {}", scriptEngineFactory.getLanguageVersion());
            LOGGER.debug("script engine names: {}", scriptEngineFactory.getNames());
        }

        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("javascript");
        scriptEngine.eval("print('hello world')");

        scriptEngine.put("globalVariable", "hello world");
        scriptEngine.eval("print('global variable: ' + globalVariable)");

        scriptEngine.eval("function hello(name) { print('function say ' + name); }");
        Invocable invocable = (Invocable) scriptEngine;
        invocable.invokeFunction("hello", "hello world");

        scriptEngine.eval("var obj = new Object()");
        scriptEngine.eval("obj.hello = function(name) { print('Hello, ' + name); }");
        Object obj = scriptEngine.get("obj");
        invocable.invokeMethod(obj, "hello", "Script Method!");

        Bindings globalScopeBindings = scriptEngine.getBindings(ScriptContext.GLOBAL_SCOPE);
        LOGGER.debug("global scope bindings: {}", globalScopeBindings.keySet());

        Bindings engineScopeBindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
        LOGGER.debug("engine scope bindings: {}", engineScopeBindings.keySet());

        Object helloObject = engineScopeBindings.get("hello");
        LOGGER.debug("hello type: {}", helloObject.getClass().getName());

        scriptEngine.eval("counter = 0");
        scriptEngine.eval("function increaseCounter() { counter = counter + 1; }");
        invocable.invokeFunction("increaseCounter");
        LOGGER.debug("counter value: {}", scriptEngine.get("counter"));
        LOGGER.debug("counter value: {}", engineScopeBindings.get("counter"));
        invocable.invokeFunction("increaseCounter");
        LOGGER.debug("counter value: {}", scriptEngine.get("counter"));
        LOGGER.debug("counter value: {}", engineScopeBindings.get("counter"));

        InputStream testScriptInputStream = ScriptingTest.class.getResourceAsStream("/test.js");
        Reader testScriptReader = new InputStreamReader(testScriptInputStream);
        scriptEngine.eval(testScriptReader);
        Object testList = scriptEngine.get("testList");
        LOGGER.debug("testList: {}", testList);
        LOGGER.debug("testList type: {}", testList.getClass().getName());
        Bindings testListBindings = (Bindings) testList;
        LOGGER.debug("testList bindings: {}", testListBindings.entrySet());
    }

    @Test
    public void testBeanshell() throws Exception {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("beanshell");
        InputStream testScriptInputStream = ScriptingTest.class.getResourceAsStream("/test.bsh");
        Reader testScriptReader = new InputStreamReader(testScriptInputStream);
        scriptEngine.eval(testScriptReader);
        Object counter = scriptEngine.get("counter");
        LOGGER.debug("counter: {}", counter);
        Bindings engineScopeBindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
        LOGGER.debug("engine scope bindings: {}", engineScopeBindings.keySet());

        Invocable invocable = (Invocable) scriptEngine;
        invocable.invokeFunction("increaseCounter");
        counter = scriptEngine.get("counter");
        LOGGER.debug("counter: {}", counter);
    }

    @Test
    public void testGroovy() throws Exception {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("groovy");

        Integer sum = (Integer) scriptEngine.eval("(1..10).sum()");
        assertEquals(Integer.valueOf(55), sum);

        scriptEngine.put("first", "HELLO");
        scriptEngine.put("second", "world");
        String result = (String) scriptEngine.eval("first.toLowerCase() + ' ' + second.toUpperCase()");
        assertEquals("hello WORLD", result);

        String fact = "def factorial(n) { n == 1 ? 1 : n * factorial(n - 1) }";
        scriptEngine.eval(fact);
        Invocable inv = (Invocable) scriptEngine;
        Object[] params = {5};
        Object facResult = inv.invokeFunction("factorial", params);
        assertEquals(Integer.valueOf(120), facResult);

        InputStream testScriptInputStream = ScriptingTest.class.getResourceAsStream("/test.groovy");
        Reader testScriptReader = new InputStreamReader(testScriptInputStream);
        scriptEngine.eval(testScriptReader);

        //LOGGER.debug("result type: {}", result2.getClass().getName());
        Bindings engineScopeBindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
        LOGGER.debug("engine scope bindings: {}", engineScopeBindings.keySet());

        Invocable invocable = (Invocable) scriptEngine;
        invocable.invokeFunction("increaseCounter", null);
        Object counter = scriptEngine.get("counter");
        LOGGER.debug("counter: {}", counter);
    }
}
