/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.integ.be.e_contract.ejsf.cdi;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.BeforeBeanDiscovery;
import jakarta.enterprise.inject.spi.Extension;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestScopeExtension implements Extension, Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestScopeExtension.class);

    public void addScope(@Observes BeforeBeanDiscovery event) {
        LOGGER.debug("registering @TestScoped");
        event.addScope(TestScoped.class, true, false);
    }

    public void registerContext(@Observes AfterBeanDiscovery event) {
        LOGGER.debug("registering TestScopeContext");
        event.addContext(new TestScopeContext());
    }
}
