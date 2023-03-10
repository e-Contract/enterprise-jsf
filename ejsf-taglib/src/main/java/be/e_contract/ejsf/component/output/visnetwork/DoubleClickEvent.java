/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.output.visnetwork;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import org.primefaces.event.AbstractAjaxBehaviorEvent;

public class DoubleClickEvent extends AbstractAjaxBehaviorEvent {

    public static final String NAME = "doubleClick";

    private final String node;

    public DoubleClickEvent(UIComponent component, Behavior behavior, String node) {
        super(component, behavior);
        this.node = node;
    }

    public String getNode() {
        return this.node;
    }
}
