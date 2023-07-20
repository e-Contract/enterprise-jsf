/*
 * Enterprise JSF project.
 *
 * Copyright 2021-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.clocksync;

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

@FacesRenderer(componentFamily = ClockSyncComponent.COMPONENT_FAMILY, rendererType = ClockSyncRenderer.RENDERER_TYPE)
public class ClockSyncRenderer extends CoreRenderer {

    public static final String RENDERER_TYPE = "ejsf.clockSyncRenderer";

    private static final Logger LOGGER = LoggerFactory.getLogger(ClockSyncRenderer.class);

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ClockSyncComponent clockSync = (ClockSyncComponent) component;
        encodeScript(context, clockSync);
    }

    private void encodeScript(FacesContext context, ClockSyncComponent clockSync) throws IOException {
        WidgetBuilder widgetBuilder = getWidgetBuilder(context);
        widgetBuilder.init("EJSFClockSync", clockSync);
        widgetBuilder.attr("SYNC_COUNT", clockSync.getSyncCount());
        widgetBuilder.attr("ACCEPTED_ROUND_TRIP_DELAY", clockSync.getAcceptedRoundTripDelay());
        widgetBuilder.attr("MAXIMUM_CLOCK_DRIFT", clockSync.getMaximumClockDrift());
        ExternalContext externalContext = context.getExternalContext();
        String contextPath = externalContext.getRequestContextPath();
        String syncEndpoint = contextPath + "/" + clockSync.getClockLocation();
        widgetBuilder.attr("SYNC_ENDPOINT", syncEndpoint);

        if (clockSync.isSessionKeepAlivePing()) {
            int sessionMaxInactiveIntervalSeconds = externalContext.getSessionMaxInactiveInterval();
            int beforeExpiryPeriod = clockSync.getSessionKeepAliveBeforeExpiryPeriod();
            sessionMaxInactiveIntervalSeconds -= beforeExpiryPeriod;
            if (sessionMaxInactiveIntervalSeconds > 0) {
                widgetBuilder.attr("SESSION_KEEP_ALIVE_PING_INTERVAL", sessionMaxInactiveIntervalSeconds * 1000);
            }
        }
        encodeClientBehaviors(context, clockSync);
        widgetBuilder.finish();
    }

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);

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
