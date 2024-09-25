/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.integ.be.e_contract.ejsf;

import test.integ.be.e_contract.ejsf.cdi.TestScoped;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Named;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@TestScoped
public class InputPeriodController implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(InputPeriodController.class);

    private Integer value;

    public Integer getValue() {
        return this.value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @PostConstruct
    public void postConstruct() {
        LOGGER.debug("post construct");
        this.value = null;
    }

    @PreDestroy
    public void preDestroy() {
        LOGGER.debug("pre destroy");
    }
}
