/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.performance;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIInput;
import javax.faces.component.behavior.Behavior;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import org.primefaces.component.api.Widget;
import org.primefaces.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(PerformanceNavigationComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "ejsf", name = "performance-navigation.js")
})
public class PerformanceNavigationComponent extends UIComponentBase implements ClientBehaviorHolder, Widget {

    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceNavigationComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.performanceNavigationComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public PerformanceNavigationComponent() {
        setRendererType(PerformanceNavigationRenderer.RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public Collection<String> getEventNames() {
        return Arrays.asList(PerformanceNavigationTimingEvent.NAME);
    }

    @Override
    public String getDefaultEventName() {
        return PerformanceNavigationTimingEvent.NAME;
    }

    @Override
    public void queueEvent(FacesEvent facesEvent) {
        FacesContext facesContext = facesEvent.getFacesContext();
        ExternalContext externalContext = facesContext.getExternalContext();
        Map<String, String> requestParameterMap = externalContext.getRequestParameterMap();
        String partialSourceParam = requestParameterMap.get(Constants.RequestParams.PARTIAL_SOURCE_PARAM);
        String clientId = getClientId(facesContext);
        if (clientId.equals(partialSourceParam) && facesEvent instanceof AjaxBehaviorEvent) {
            AjaxBehaviorEvent ajaxBehaviorEvent = (AjaxBehaviorEvent) facesEvent;
            String eventName = requestParameterMap.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            if (PerformanceNavigationTimingEvent.NAME.equals(eventName)) {

                Double startTime = getParamValue(requestParameterMap, clientId, "startTime");
                if (startTime == null) {
                    return;
                }

                Double duration = getParamValue(requestParameterMap, clientId, "duration");
                if (duration == null) {
                    return;
                }

                Double loadEventStart = getParamValue(requestParameterMap, clientId, "loadEventStart");
                if (loadEventStart == null) {
                    return;
                }

                Double loadEventEnd = getParamValue(requestParameterMap, clientId, "loadEventEnd");
                if (loadEventEnd == null) {
                    return;
                }

                Double domInteractive = getParamValue(requestParameterMap, clientId, "domInteractive");
                if (domInteractive == null) {
                    return;
                }

                Double domComplete = getParamValue(requestParameterMap, clientId, "domComplete");
                if (domComplete == null) {
                    return;
                }

                Double requestStart = getParamValue(requestParameterMap, clientId, "requestStart");
                if (requestStart == null) {
                    return;
                }

                Double responseStart = getParamValue(requestParameterMap, clientId, "responseStart");
                if (responseStart == null) {
                    return;
                }

                Double responseEnd = getParamValue(requestParameterMap, clientId, "responseEnd");
                if (responseEnd == null) {
                    return;
                }

                Behavior behavior = ajaxBehaviorEvent.getBehavior();
                PerformanceNavigationTimingEvent timingEvent = new PerformanceNavigationTimingEvent(this, behavior);

                timingEvent.setStartTime(startTime);
                timingEvent.setDuration(duration);
                timingEvent.setLoadEventStart(loadEventStart);
                timingEvent.setLoadEventEnd(loadEventEnd);
                timingEvent.setDomInteractive(domInteractive);
                timingEvent.setDomComplete(domComplete);
                timingEvent.setRequestStart(requestStart);
                timingEvent.setResponseStart(responseStart);
                timingEvent.setResponseEnd(responseEnd);

                timingEvent.setPhaseId(facesEvent.getPhaseId());
                super.queueEvent(timingEvent);
                return;
            }
        }
        super.queueEvent(facesEvent);
    }

    private Double getParamValue(Map<String, String> requestParameterMap, String clientId, String paramName) {
        String param = requestParameterMap.get(clientId + "_" + paramName);
        if (UIInput.isEmpty(param)) {
            LOGGER.warn("missing parameter: " + param);
            return null;
        }
        double paramValue = Double.parseDouble(param);
        return paramValue;
    }
}
