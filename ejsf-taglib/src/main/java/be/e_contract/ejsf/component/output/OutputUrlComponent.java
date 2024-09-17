/*
 * Enterprise JSF project.
 *
 * Copyright 2023-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.output;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent(OutputUrlComponent.COMPONENT_TYPE)
public class OutputUrlComponent extends UIOutput implements ClientBehaviorHolder {

    public static final String COMPONENT_TYPE = "ejsf.outputUrl";

    public static final String COMPONENT_FAMILY = "ejsf";

    public OutputUrlComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public Collection<String> getEventNames() {
        return Arrays.asList("click");
    }

    @Override
    public String getDefaultEventName() {
        return "click";
    }

    enum PropertyKeys {
        style,
        styleClass,
        label,
        newTab
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

    public String getLabel() {
        return (String) getStateHelper().eval(PropertyKeys.label, null);
    }

    public void setLabel(String label) {
        getStateHelper().put(PropertyKeys.label, label);
    }

    public boolean isNewTab() {
        return (Boolean) getStateHelper().eval(PropertyKeys.newTab, false);
    }

    public void setNewTab(boolean newTab) {
        getStateHelper().put(PropertyKeys.newTab, newTab);
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        if (!isRendered()) {
            return;
        }
        String url = (String) getValue();
        String clientId = getClientId(context);

        ResponseWriter responseWriter = context.getResponseWriter();
        responseWriter.startElement("a", this);
        responseWriter.writeAttribute("id", clientId, "id");

        Map<String, List<ClientBehavior>> behaviors = getClientBehaviors();
        if (behaviors.containsKey("click")) {
            ClientBehaviorContext behaviorContext = ClientBehaviorContext.createClientBehaviorContext(context, this, "click", clientId, null);
            String click = behaviors.get("click").get(0).getScript(behaviorContext);
            responseWriter.writeAttribute("onclick", click, null);
        }

        String style = getStyle();
        if (null != style) {
            responseWriter.writeAttribute("style", style, "style");
        }
        String styleClass = getStyleClass();
        if (null != styleClass) {
            responseWriter.writeAttribute("class", styleClass, "styleClass");
        }

        boolean newTab = isNewTab();
        if (newTab) {
            responseWriter.writeAttribute("target", "_blank", "newTab");
        }

        responseWriter.writeAttribute("href", url, "value");
        String label = getLabel();
        if (null == label) {
            label = url;
        }
        responseWriter.writeText(label, null);
        responseWriter.endElement("a");
    }
}
