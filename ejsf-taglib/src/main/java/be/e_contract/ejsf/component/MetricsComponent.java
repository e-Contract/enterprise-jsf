/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component;

import be.e_contract.ejsf.api.metrics.Metric;
import be.e_contract.ejsf.impl.metrics.MetricsController;
import java.util.Collections;
import java.util.List;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(MetricsComponent.COMPONENT_TYPE)
public class MetricsComponent extends UIComponentBase implements NamingContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.metrics";

    @Override
    public String getFamily() {
        return UINamingContainer.COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        metrics,
    }

    public List<Metric> getMetrics() {
        List<Metric> metrics = (List<Metric>) getStateHelper().get(PropertyKeys.metrics);
        if (null != metrics) {
            return metrics;
        }
        return collectMetrics();
    }

    public void refresh() {
        collectMetrics();
    }

    private List<Metric> collectMetrics() {
        FacesContext facesContext = getFacesContext();
        ELContext elContext = facesContext.getELContext();
        Application application = facesContext.getApplication();
        ExpressionFactory expressionFactory = application.getExpressionFactory();
        ValueExpression valueExpression = expressionFactory.createValueExpression(elContext,
                "#{ejsfMetricsController}", MetricsController.class);
        MetricsController metricsController = (MetricsController) valueExpression.getValue(elContext);
        List<Metric> metrics;
        if (null == metricsController) {
            LOGGER.warn("MetricsController lookup failed");
            metrics = Collections.EMPTY_LIST;
        } else {
            metrics = metricsController.getRuntimeMetrics();
        }
        getStateHelper().put(PropertyKeys.metrics, metrics);
        return metrics;
    }
}
