/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.integ.be.e_contract.ejsf;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;

@Named
public class TestController {

    private String value;

    @PostConstruct
    public void postConstruct() {
        this.value = "hello world";
    }

    public String getValue() {
        return this.value;
    }
}
