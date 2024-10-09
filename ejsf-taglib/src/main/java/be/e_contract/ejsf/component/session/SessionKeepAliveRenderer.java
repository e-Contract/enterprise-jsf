/*
 * Enterprise JSF project.
 *
 * Copyright 2023-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.session;

import java.io.IOException;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.render.FacesRenderer;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesRenderer(componentFamily = SessionKeepAliveComponent.COMPONENT_FAMILY, rendererType = SessionKeepAliveRenderer.RENDERER_TYPE)
public class SessionKeepAliveRenderer extends CoreRenderer {

    public static final String RENDERER_TYPE = "ejsf.sessionKeepAliveRenderer";

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionKeepAliveRenderer.class);

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        SessionKeepAliveComponent sessionKeepAliveComponent = (SessionKeepAliveComponent) component;

        WidgetBuilder widgetBuilder = getWidgetBuilder(context);
        widgetBuilder.init("EJSFSessionKeepAlive", sessionKeepAliveComponent);
        ExternalContext externalContext = context.getExternalContext();
        int sessionMaxInactiveIntervalSeconds = externalContext.getSessionMaxInactiveInterval();
        LOGGER.debug("session max inactive interval: {} sec", sessionMaxInactiveIntervalSeconds);
        int pingPeriodBeforeExpiry = sessionKeepAliveComponent.getPingPeriodBeforeExpiry();
        sessionMaxInactiveIntervalSeconds -= pingPeriodBeforeExpiry;
        if (sessionMaxInactiveIntervalSeconds > 0) {
            widgetBuilder.attr("SESSION_KEEP_ALIVE_PING_INTERVAL", sessionMaxInactiveIntervalSeconds * 1000);
        } else {
            LOGGER.warn("pingPeriodBeforeExpiry value to large: {}", pingPeriodBeforeExpiry);
        }
        int maxKeepAlivePeriod = sessionKeepAliveComponent.getMaxKeepAlivePeriod();
        if (maxKeepAlivePeriod > 0) {
            widgetBuilder.attr("MAX_KEEP_ALIVE_PERIOD", maxKeepAlivePeriod);
        }
        widgetBuilder.finish();
    }

    @Override
    public void decode(FacesContext context, UIComponent component) {
        ExternalContext externalContext = context.getExternalContext();
        Map<String, String> params
                = externalContext.getRequestParameterMap();
        String behaviorSource = params.get(
                ClientBehaviorContext.BEHAVIOR_SOURCE_PARAM_NAME);
        if (null == behaviorSource) {
            return;
        }
        String clientId = component.getClientId(context);
        if (!behaviorSource.equals(clientId)) {
            return;
        }
        String behaviorEvent = params.get(
                ClientBehaviorContext.BEHAVIOR_EVENT_PARAM_NAME);
        if (null == behaviorEvent) {
            return;
        }
        if ("keepAlive".equals(behaviorEvent)) {
            String sessionId = externalContext.getSessionId(false);
            LOGGER.debug("keeping session alive: {}", sessionId);
        }
    }
}
