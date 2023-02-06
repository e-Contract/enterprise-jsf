/*
 * Enterprise JSF project.
 *
 * Copyright 2021-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.echarts;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import org.primefaces.event.AbstractAjaxBehaviorEvent;

public class EChartsClickEvent extends AbstractAjaxBehaviorEvent {

    public static final String NAME = "click";

    private final String name;

    private final int dataIndex;

    public EChartsClickEvent(UIComponent component, Behavior behavior, String name, int dataIndex) {
        super(component, behavior);
        this.name = name;
        this.dataIndex = dataIndex;
    }

    public String getName() {
        return this.name;
    }

    public int getDataIndex() {
        return this.dataIndex;
    }
}