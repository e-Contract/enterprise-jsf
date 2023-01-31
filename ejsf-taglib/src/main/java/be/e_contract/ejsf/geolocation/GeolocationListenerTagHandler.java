/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.geolocation;

import javax.faces.component.UIComponent;
import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.MetaRule;
import javax.faces.view.facelets.MetaRuleset;
import org.primefaces.facelets.MethodRule;

public class GeolocationListenerTagHandler extends ComponentHandler {

    public GeolocationListenerTagHandler(ComponentConfig config) {
        super(config);
    }

    @Override
    protected MetaRuleset createMetaRuleset(Class type) {
        MetaRuleset metaRuleset = super.createMetaRuleset(type);
        MetaRule metaRule = new MethodRule("action", void.class, new Class[]{GeolocationEvent.class});
        metaRuleset.addRule(metaRule);
        return metaRuleset;
    }

    @Override
    public void onComponentCreated(FaceletContext faceletContext, UIComponent component, UIComponent parent) {
        super.onComponentCreated(faceletContext, component, parent);
        GeolocationComponent geolocationComponent = (GeolocationComponent) parent;
        GeolocationListenerComponent geolocationListenerComponent = (GeolocationListenerComponent) component;
        geolocationComponent.addGeolocationEventListener(new GeolocationListenerAdapter(geolocationListenerComponent.getAction()));
    }
}
