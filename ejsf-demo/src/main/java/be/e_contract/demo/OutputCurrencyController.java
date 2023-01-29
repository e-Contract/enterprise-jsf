/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import javax.inject.Named;

@Named("outputCurrencyController")
public class OutputCurrencyController {

    public Double getValue() {
        return Math.PI;
    }
}
