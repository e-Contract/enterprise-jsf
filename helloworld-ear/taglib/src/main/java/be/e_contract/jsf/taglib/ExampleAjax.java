package be.e_contract.jsf.taglib;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent(ExampleAjax.COMPONENT_TYPE)
public class ExampleAjax extends UIComponentBase implements ClientBehaviorHolder {

    public static final String COMPONENT_TYPE = "ejsf.exampleAjax";

    public static final String COMPONENT_FAMILY = "ejsf";

    public ExampleAjax() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        String clientId = super.getClientId(context);

        ResponseWriter responseWriter = context.getResponseWriter();
        responseWriter.startElement("button", this);
        responseWriter.writeAttribute("id", clientId, "id");
        responseWriter.writeAttribute("type", "button", null);

        Map<String, List<ClientBehavior>> behaviors = getClientBehaviors();
        if (behaviors.containsKey("click")) {
            ClientBehaviorContext behaviorContext
                    = ClientBehaviorContext.createClientBehaviorContext(
                            context, this, "click", clientId, null);
            String click = behaviors.get("click").get(0).getScript(behaviorContext);
            responseWriter.writeAttribute("onclick", click, null);
        }

        responseWriter.write("Click me");
        responseWriter.endElement("button");
    }

    @Override
    public void decode(FacesContext context) {
        Map<String, List<ClientBehavior>> allClientBehaviors = getClientBehaviors();
        if (allClientBehaviors.isEmpty()) {
            return;
        }
        ExternalContext externalContext = context.getExternalContext();
        Map<String, String> params = externalContext.getRequestParameterMap();
        String behaviorEvent = params.get(
                ClientBehaviorContext.BEHAVIOR_EVENT_PARAM_NAME);
        if (null == behaviorEvent) {
            return;
        }
        List<ClientBehavior> clientBehaviors = allClientBehaviors.get(behaviorEvent);
        if (clientBehaviors.isEmpty()) {
            return;
        }
        String behaviorSource = params.get(
                ClientBehaviorContext.BEHAVIOR_SOURCE_PARAM_NAME);
        if (null == behaviorSource) {
            return;
        }
        String clientId = getClientId(context);
        if (!behaviorSource.equals(clientId)) {
            return;
        }
        for (ClientBehavior clientBehavior : clientBehaviors) {
            clientBehavior.decode(context, this);
        }
    }

    @Override
    public Collection<String> getEventNames() {
        return Arrays.asList("click");
    }

    @Override
    public String getDefaultEventName() {
        return "click";
    }
}
