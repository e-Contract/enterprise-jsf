/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.output.echarts;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIOutput;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import org.primefaces.component.api.Widget;
import org.primefaces.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(EChartsComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "primefaces", name = "components.js"),
    @ResourceDependency(library = "echarts", name = "echarts.min.js"),
    @ResourceDependency(library = "ejsf", name = "echarts.js")
})
public class EChartsComponent extends UIOutput implements Widget, ClientBehaviorHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(EChartsComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.echartsComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public EChartsComponent() {
        setRendererType(EChartsRenderer.RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        widgetVar,
        width,
        height,
        style,
        styleClass,
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public Integer getWidth() {
        return (Integer) getStateHelper().eval(PropertyKeys.width, null);
    }

    public void setWidth(Integer width) {
        getStateHelper().put(PropertyKeys.width, width);
    }

    public Integer getHeight() {
        return (Integer) getStateHelper().eval(PropertyKeys.height, null);
    }

    public void setHeight(Integer height) {
        getStateHelper().put(PropertyKeys.height, height);
    }

    public String getStyle() {
        return (String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(String style) {
        getStateHelper().put(PropertyKeys.style, style);
    }

    public String getStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(String styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, styleClass);
    }

    @Override
    public Collection<String> getEventNames() {
        return Arrays.asList(EChartsClickEvent.NAME);
    }

    @Override
    public String getDefaultEventName() {
        return EChartsClickEvent.NAME;
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
            if (EChartsClickEvent.NAME.equals(eventName)) {
                String nameParam = requestParameterMap.get(clientId + "_name");
                if (UIInput.isEmpty(nameParam)) {
                    LOGGER.warn("missing name parameter");
                    return;
                }
                String dataIndexParam = requestParameterMap.get(clientId + "_dataIndex");
                if (UIInput.isEmpty(dataIndexParam)) {
                    LOGGER.debug("missing dataIndex parameter");
                    return;
                }
                int dataIndex = Integer.parseInt(dataIndexParam);
                String seriesIndexParam = requestParameterMap.get(clientId + "_seriesIndex");
                if (UIInput.isEmpty(seriesIndexParam)) {
                    LOGGER.debug("missing seriesIndex parameter");
                    return;
                }
                int seriesIndex = Integer.parseInt(seriesIndexParam);

                EChartsClickEvent eChartsClickEvent
                        = new EChartsClickEvent(this, behaviorEvent.getBehavior(),
                                nameParam, seriesIndex, dataIndex);
                eChartsClickEvent.setPhaseId(facesEvent.getPhaseId());
                super.queueEvent(eChartsClickEvent);
                return;
            }
        }
        super.queueEvent(facesEvent);
    }
}
