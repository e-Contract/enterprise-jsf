/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.taginfo;

import java.io.Serializable;

public class FunctionInfo implements Serializable {

    private final String name;
    private final String functionClass;
    private final String signature;
    private final String description;

    public FunctionInfo(String name, String functionClass, String signature, String description) {
        this.name = name;
        this.functionClass = functionClass;
        this.signature = signature;
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public String getFunctionClass() {
        return this.functionClass;
    }

    public String getSignature() {
        return this.signature;
    }

    public String getDescription() {
        return this.description;
    }
}
