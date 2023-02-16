/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.output;

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

@FacesComponent(OutputEmailComponent.COMPONENT_TYPE)
public class OutputEmailComponent extends UIOutput implements ClientBehaviorHolder {

    public static final String COMPONENT_TYPE = "ejsf.outputEmail";

    public static final String COMPONENT_FAMILY = "ejsf";

    public OutputEmailComponent() {
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
        subject,
        cc,
        bcc,
        body,
        style,
        styleClass,
        label
    }

    public void setSubject(String subject) {
        getStateHelper().put(PropertyKeys.subject, subject);
    }

    public String getSubject() {
        return (String) getStateHelper().eval(PropertyKeys.subject);
    }

    public void setBcc(String bcc) {
        getStateHelper().put(PropertyKeys.bcc, bcc);
    }

    public String getBcc() {
        return (String) getStateHelper().eval(PropertyKeys.bcc);
    }

    public void setCc(String cc) {
        getStateHelper().put(PropertyKeys.cc, cc);
    }

    public String getCc() {
        return (String) getStateHelper().eval(PropertyKeys.cc);
    }

    public void setBody(String body) {
        getStateHelper().put(PropertyKeys.body, body);
    }

    public String getBody() {
        return (String) getStateHelper().eval(PropertyKeys.body);
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

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
        String email = (String) getValue();
        String clientId = super.getClientId(context);
        String url = "mailto:" + email;
        String subject = getSubject();
        if (null != subject) {
            url = appendUrlParameter(url, "subject", subject);
        }
        String cc = getCc();
        if (null != cc) {
            url = appendUrlParameter(url, "cc", cc);
        }
        String bcc = getBcc();
        if (null != bcc) {
            url = appendUrlParameter(url, "bcc", bcc);
        }
        String body = getBody();
        if (null != body) {
            url = appendUrlParameter(url, "body", body);
        }

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

        responseWriter.writeAttribute("href", url, "value");
        String label = getLabel();
        if (null == label) {
            label = email;
        }
        responseWriter.write(label);
        responseWriter.endElement("a");
    }

    private String appendUrlParameter(String url, String parameter, String value) {
        if (!url.contains("?")) {
            return url + "?" + parameter + "=" + value;
        }
        return url + "&" + parameter + "=" + value;
    }
}
