/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.storage;

import java.util.HashMap;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import javax.faces.event.AjaxBehaviorEvent;

public class StorageGetItemsEvent extends AjaxBehaviorEvent {

    public static final String NAME = "items";

    private final Map<String, String> items;

    public StorageGetItemsEvent(UIComponent component, Behavior behavior) {
        super(component, behavior);
        this.items = new HashMap<>();
    }

    public String getItem(String name) {
        return this.items.get(name);
    }

    void addItem(String name, String value) {
        this.items.put(name, value);
    }
}
