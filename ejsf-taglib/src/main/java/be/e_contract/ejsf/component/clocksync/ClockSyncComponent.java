/*
 * Enterprise JSF project.
 *
 * Copyright 2021-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.clocksync;

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

/**
 * Clock synchronization component. PrimeFaces also has a clock component, but
 * we construct our own for the following reasons:
 * <ul>
 * <li>we don't need the visual aspect of the primefaces clock</li>
 * <li>primefaces clock synchronizes via AJAX calls, we want a more direct
 * syncing the prevent loosing milliseconds due to the JSF lifecycle</li>
 * <li>primefaces clock does not provide a widgetVar attribute, and hence the
 * client-side aspect is not available while this is exactly what we are looking
 * for</li>
 * <li>we want a nice Javascript interface to ease calculating time diffs
 * between client and server</li>
 * </ul>
 *
 * @author Frank Cornelis
 */
@FacesComponent(ClockSyncComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "ejsf", name = "clock-sync.js")
})
public class ClockSyncComponent extends UIComponentBase implements Widget, ClientBehaviorHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClockSyncComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.clockSync";

    public static final String COMPONENT_FAMILY = "ejsf";

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        widgetVar,
        syncCount,
        acceptedRoundTripDelay,
        maximumClockDrift,
        sessionKeepAlivePing,
        clockLocation,
    }

    public boolean isSessionKeepAlivePing() {
        return (Boolean) getStateHelper().eval(PropertyKeys.sessionKeepAlivePing, false);
    }

    public void setSessionKeepAlivePing(boolean sessionKeepAlivePing) {
        getStateHelper().put(PropertyKeys.sessionKeepAlivePing, sessionKeepAlivePing);
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public int getSyncCount() {
        return (Integer) getStateHelper().eval(PropertyKeys.syncCount, 5);
    }

    public void setSyncCount(int syncCount) {
        getStateHelper().put(PropertyKeys.syncCount, syncCount);
    }

    public int getAcceptedRoundTripDelay() {
        return (Integer) getStateHelper().eval(PropertyKeys.acceptedRoundTripDelay, 1000);
    }

    public void setAcceptedRoundTripDelay(int acceptedRoundTripDelay) {
        getStateHelper().put(PropertyKeys.acceptedRoundTripDelay, acceptedRoundTripDelay);
    }

    public int getMaximumClockDrift() {
        return (Integer) getStateHelper().eval(PropertyKeys.maximumClockDrift, 1000);
    }

    public void setMaximumClockDrift(int maximumClockDrift) {
        getStateHelper().put(PropertyKeys.maximumClockDrift, maximumClockDrift);
    }

    public String getClockLocation() {
        return (String) getStateHelper().eval(PropertyKeys.clockLocation);
    }

    public void setClockLocation(String clockLocation) {
        getStateHelper().put(PropertyKeys.clockLocation, clockLocation);
    }

    @Override
    public Collection<String> getEventNames() {
        return Arrays.asList(ClockSyncEvent.NAME);
    }

    @Override
    public String getDefaultEventName() {
        return ClockSyncEvent.NAME;
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
            String eventName = requestParameterMap.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = getClientId(facesContext);
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) facesEvent;
            if (ClockSyncEvent.NAME.equals(eventName)) {
                String bestRoundTripDelayParam = requestParameterMap.get(clientId + "_bestRoundTripDelay");
                if (UIInput.isEmpty(bestRoundTripDelayParam)) {
                    LOGGER.warn("missing bestRoundTripDelay parameter");
                    return;
                }
                String deltaTParam = requestParameterMap.get(clientId + "_deltaT");
                if (UIInput.isEmpty(deltaTParam)) {
                    LOGGER.debug("missing deltaT parameter");
                    return;
                }
                long bestRoundTripDelay = Long.parseLong(bestRoundTripDelayParam);
                long deltaT = Long.parseLong(deltaTParam);
                LOGGER.debug("queueing ClockSyncEvent: {} - {}", bestRoundTripDelay, deltaT);

                ClockSyncEvent clockSyncEvent
                        = new ClockSyncEvent(this, behaviorEvent.getBehavior(),
                                bestRoundTripDelay, deltaT);
                clockSyncEvent.setPhaseId(facesEvent.getPhaseId());
                super.queueEvent(clockSyncEvent);
                return;
            }
        }
        super.queueEvent(facesEvent);
    }
}
