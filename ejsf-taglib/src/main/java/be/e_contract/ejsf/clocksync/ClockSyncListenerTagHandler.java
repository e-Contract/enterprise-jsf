/*
 * Enterprise JSF project.
 *
 * Copyright 2021-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.clocksync;

import javax.faces.component.UIComponent;
import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.MetaRule;
import javax.faces.view.facelets.MetaRuleset;
import org.primefaces.facelets.MethodRule;

public class ClockSyncListenerTagHandler extends ComponentHandler {

    public ClockSyncListenerTagHandler(ComponentConfig config) {
        super(config);
    }

    @Override
    protected MetaRuleset createMetaRuleset(Class type) {
        MetaRuleset metaRuleset = super.createMetaRuleset(type);
        MetaRule metaRule = new MethodRule("action", void.class, new Class[]{ClockSyncEvent.class});
        metaRuleset.addRule(metaRule);
        return metaRuleset;
    }

    @Override
    public void onComponentCreated(FaceletContext faceletContext, UIComponent component, UIComponent parent) {
        super.onComponentCreated(faceletContext, component, parent);
        ClockSyncComponent clockSyncComponent = (ClockSyncComponent) parent;
        ClockSyncListenerComponent clockSyncListenerComponent = (ClockSyncListenerComponent) component;
        clockSyncComponent.addClockSyncListener(new ClockSyncListenerAdapter(clockSyncListenerComponent.getAction()));
    }
}
