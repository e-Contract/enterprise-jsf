/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.integ.be.e_contract.ejsf.cdi;

import jakarta.enterprise.context.spi.Context;
import jakarta.enterprise.context.spi.Contextual;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.Bean;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestScopeContext implements Context, Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestScopeContext.class);

    private static Map<Class, TestScopeInstance> instances;

    @Override
    public Class<? extends Annotation> getScope() {
        return TestScoped.class;
    }

    @Override
    public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
        T beanInstance = get(contextual);
        if (null != beanInstance) {
            return beanInstance;
        }
        Bean bean = (Bean) contextual;
        Class beanClass = bean.getBeanClass();
        LOGGER.debug("creating bean instance of type: {}", beanClass.getName());
        beanInstance = (T) bean.create(creationalContext);
        TestScopeInstance testScopeInstance = new TestScopeInstance();
        testScopeInstance.bean = bean;
        testScopeInstance.creationalContext = creationalContext;
        testScopeInstance.instance = beanInstance;
        TestScopeContext.instances.put(beanClass, testScopeInstance);
        return beanInstance;
    }

    @Override
    public <T> T get(Contextual<T> contextual) {
        Bean bean = (Bean) contextual;
        Class beanClass = bean.getBeanClass();
        TestScopeInstance<T> testScopeInstance = TestScopeContext.instances.get(beanClass);
        if (null == testScopeInstance) {
            return null;
        }
        return testScopeInstance.instance;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    public void handleStartTestEvent(@Observes StartTestEvent event) {
        LOGGER.debug("start test: {}", this);
        TestScopeContext.instances = new HashMap<>();
    }

    public void handleStopTestEvent(@Observes StopTestEvent event) {
        LOGGER.debug("stop test: {}", this);
        Collection<TestScopeInstance> instances = TestScopeContext.instances.values();
        for (TestScopeInstance instance : instances) {
            Bean bean = instance.bean;
            Class beanClass = bean.getBeanClass();
            LOGGER.debug("destroying bean instance of type: {}", beanClass.getName());
            bean.destroy(instance.instance, instance.creationalContext);
        }
        TestScopeContext.instances.clear();
    }

    private static class TestScopeInstance<T> {

        private Bean<T> bean;
        private T instance;
        private CreationalContext<T> creationalContext;
    }
}
