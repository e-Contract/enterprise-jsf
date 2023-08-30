/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.api.metrics;

import java.io.Serializable;

public class Metric implements Serializable {

    private final String name;
    private final String value;
    private final String description;

    public Metric(String name, String value, String description) {
        this.name = name;
        this.value = value;
        this.description = description;
    }

    public Metric(String name, String value) {
        this(name, value, null);
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public String getDescription() {
        return this.description;
    }
}
