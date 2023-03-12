/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.jmsinfo;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import org.primefaces.event.AbstractAjaxBehaviorEvent;

public class ReplayEvent extends AbstractAjaxBehaviorEvent {

    public static final String NAME = "replay";

    private final String messageId;

    public ReplayEvent(UIComponent component, Behavior behavior, String messageId) {
        super(component, behavior);
        this.messageId = messageId;
    }

    public String getMessageId() {
        return this.messageId;
    }
}
