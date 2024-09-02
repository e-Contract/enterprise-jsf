/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.storage;

import java.io.Serializable;
import javax.el.ValueExpression;

public class StorageItem implements Serializable {

    private final String type;

    private final String name;

    private final ValueExpression value;

    public StorageItem(String name, String type, ValueExpression value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public ValueExpression getValue() {
        return this.value;
    }
}
