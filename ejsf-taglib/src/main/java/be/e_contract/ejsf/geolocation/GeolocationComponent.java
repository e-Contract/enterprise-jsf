/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.geolocation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIInput;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import org.primefaces.component.api.Widget;
import org.primefaces.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(GeolocationComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "ejsf", name = "geolocation.js")
})
public class GeolocationComponent extends UIComponentBase implements Widget, ClientBehaviorHolder {

    public static final String COMPONENT_TYPE = "ejsf.geolocationComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    private static final Logger LOGGER = LoggerFactory.getLogger(GeolocationComponent.class);

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public Collection<String> getEventNames() {
        return Arrays.asList(GeolocationAjaxBehaviorEvent.NAME);
    }

    @Override
    public String getDefaultEventName() {
        return GeolocationAjaxBehaviorEvent.NAME;
    }

    @Override
    public void processDecodes(final FacesContext fc) {
        if (isSelfRequest(fc)) {
            decode(fc);
        } else {
            super.processDecodes(fc);
        }
    }

    @Override
    public void processValidators(final FacesContext fc) {
        if (!isSelfRequest(fc)) {
            super.processValidators(fc);
        }
    }

    @Override
    public void processUpdates(final FacesContext fc) {
        if (!isSelfRequest(fc)) {
            super.processUpdates(fc);
        }
    }

    private boolean isSelfRequest(FacesContext facesContext) {
        String clientId = getClientId(facesContext);
        ExternalContext externalContext = facesContext.getExternalContext();
        Map<String, String> requestParameterMap = externalContext.getRequestParameterMap();
        String partialSourceParam = requestParameterMap.get(Constants.RequestParams.PARTIAL_SOURCE_PARAM);
        return clientId.equals(partialSourceParam);
    }

    @Override
    public void queueEvent(FacesEvent facesEvent) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (isSelfRequest(facesContext) && facesEvent instanceof AjaxBehaviorEvent) {
            ExternalContext externalContext = facesContext.getExternalContext();
            Map<String, String> requestParameterMap = externalContext.getRequestParameterMap();
            final String eventName = requestParameterMap.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            final String clientId = getClientId(facesContext);
            final AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) facesEvent;
            if (GeolocationAjaxBehaviorEvent.NAME.equals(eventName)) {
                String latitudeParam = requestParameterMap.get(clientId + "_latitude");
                if (UIInput.isEmpty(latitudeParam)) {
                    LOGGER.warn("missing latitude parameter");
                    return;
                }
                double latitude = Double.parseDouble(latitudeParam);

                String longitudeParam = requestParameterMap.get(clientId + "_longitude");
                if (UIInput.isEmpty(longitudeParam)) {
                    LOGGER.warn("missing longitude parameter");
                    return;
                }
                double longitude = Double.parseDouble(longitudeParam);

                String accuracyParam = requestParameterMap.get(clientId + "_accuracy");
                if (UIInput.isEmpty(accuracyParam)) {
                    LOGGER.warn("missing accuracy parameter");
                    return;
                }
                double accuracy = Double.parseDouble(accuracyParam);

                final GeolocationAjaxBehaviorEvent geolocationAjaxBehaviorEvent
                        = new GeolocationAjaxBehaviorEvent(this, behaviorEvent.getBehavior(),
                                latitude, longitude, accuracy);
                geolocationAjaxBehaviorEvent.setPhaseId(facesEvent.getPhaseId());
                super.queueEvent(geolocationAjaxBehaviorEvent);

                return;
            }
        }
        super.queueEvent(facesEvent);
    }
}
