/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.matomo;

import java.io.Serializable;

public class CustomDimension implements Serializable {

    private final int dimension;
    private final String value;

    public CustomDimension(int dimension, String value) {
        this.dimension = dimension;
        this.value = value;
    }

    public int getDimension() {
        return this.dimension;
    }

    public String getValue() {
        return this.value;
    }
}
