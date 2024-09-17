package be.e_contract.jsf.taglib;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.faces.application.Application;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.behavior.AjaxBehavior;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.component.search.SearchExpressionContext;
import javax.faces.component.search.SearchExpressionHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;

@FacesComponent(ExampleAjaxEventComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "javax.faces", name = "jsf.js"),
    @ResourceDependency(library = "ejsf", name = "example-ajax-event.js")
})
public class ExampleAjaxEventComponent extends UIComponentBase implements ClientBehaviorHolder {

    public static final String COMPONENT_TYPE = "ejsf.exampleAjaxEvent";

    public static final String COMPONENT_FAMILY = "ejsf";

    public ExampleAjaxEventComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        if (!isRendered()) {
            return;
        }
        String clientId = super.getClientId(context);
        ResponseWriter responseWriter = context.getResponseWriter();
        responseWriter.startElement("button", this);
        responseWriter.writeAttribute("id", clientId, "id");
        responseWriter.writeAttribute("type", "button", null);

        Map<String, List<ClientBehavior>> behaviors = getClientBehaviors();
        if (behaviors.containsKey(ExampleAjaxBehaviorEvent.NAME)) {
            AjaxBehavior ajaxBehavior = (AjaxBehavior) behaviors.get(ExampleAjaxBehaviorEvent.NAME).get(0);
            String renderParam = resolveClientIds(context, ajaxBehavior.getRender());
            String executeParam = resolveClientIds(context, ajaxBehavior.getExecute());
            responseWriter.writeAttribute("onclick", "exampleAjaxEventOnClick('"
                    + clientId + "',event," + executeParam + "," + renderParam + ")", null);
        }

        responseWriter.writeText("Click me", null);
        responseWriter.endElement("button");
    }

    private String resolveClientIds(FacesContext facesContext, Collection<String> relativeIds) {
        if (null == relativeIds) {
            return "null";
        }
        if (relativeIds.isEmpty()) {
            return "null";
        }
        Application application = facesContext.getApplication();
        SearchExpressionHandler searchExpressionHandler
                = application.getSearchExpressionHandler();
        SearchExpressionContext searchExpressionContext
                = SearchExpressionContext
                        .createSearchExpressionContext(facesContext, this);
        StringBuilder absoluteClientIds = new StringBuilder();
        for (String relativeId : relativeIds) {
            List<String> clientIdList = searchExpressionHandler
                    .resolveClientIds(searchExpressionContext, relativeId);
            for (String clientId : clientIdList) {
                if (absoluteClientIds.length() > 0) {
                    absoluteClientIds.append(' ');
                }
                absoluteClientIds.append(clientId);
            }
        }
        return "'" + absoluteClientIds.toString() + "'";
    }

    @Override
    public void decode(FacesContext context) {
        ExternalContext externalContext = context.getExternalContext();
        Map<String, String> params = externalContext.getRequestParameterMap();
        String behaviorSource = params.get(
                ClientBehaviorContext.BEHAVIOR_SOURCE_PARAM_NAME);
        if (null == behaviorSource) {
            return;
        }
        String clientId = getClientId(context);
        if (!behaviorSource.equals(clientId)) {
            return;
        }
        String behaviorEvent = params.get(
                ClientBehaviorContext.BEHAVIOR_EVENT_PARAM_NAME);
        if (null == behaviorEvent) {
            return;
        }
        Map<String, List<ClientBehavior>> allClientBehaviors = getClientBehaviors();
        List<ClientBehavior> clientBehaviors = allClientBehaviors.get(behaviorEvent);
        if (null == clientBehaviors) {
            return;
        }
        for (ClientBehavior clientBehavior : clientBehaviors) {
            clientBehavior.decode(context, this);
        }
    }

    @Override
    public Collection<String> getEventNames() {
        return Arrays.asList(ExampleAjaxBehaviorEvent.NAME);
    }

    @Override
    public String getDefaultEventName() {
        return ExampleAjaxBehaviorEvent.NAME;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        Map<String, String> requestParameterMap
                = externalContext.getRequestParameterMap();
        String clientId = getClientId(facesContext);
        String eventName = requestParameterMap.get(
                ClientBehaviorContext.BEHAVIOR_EVENT_PARAM_NAME);
        if (ExampleAjaxBehaviorEvent.NAME.equals(eventName)) {
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
            String parameter = requestParameterMap.get(clientId + "_parameter");
            ExampleAjaxBehaviorEvent exampleAjaxBehaviorEvent
                    = new ExampleAjaxBehaviorEvent(this, behaviorEvent.getBehavior(), parameter);
            PhaseId phaseId = behaviorEvent.getPhaseId();
            exampleAjaxBehaviorEvent.setPhaseId(phaseId);
            super.queueEvent(exampleAjaxBehaviorEvent);
            return;
        }
        super.queueEvent(event);
    }
}
