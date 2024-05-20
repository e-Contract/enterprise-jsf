/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import jakarta.inject.Named;

@Named("scriptController")
public class ScriptController {

    public String getValue() {
        return "hello world from CDI";
    }
}
