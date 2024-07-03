/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.input;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIInput;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ComponentSystemEventListener;
import javax.faces.event.ListenerFor;
import javax.faces.event.PostAddToViewEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(InputComponent.COMPONENT_TYPE)
@ListenerFor(systemEventClass = PostAddToViewEvent.class)
public class InputComponent extends UIInput implements NamingContainer, ComponentSystemEventListener {

    public static final String COMPONENT_TYPE = "ejsf.inputComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    private static final Logger LOGGER = LoggerFactory.getLogger(InputComponent.class);

    public InputComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public enum PropertyKeys {
        type,
        types,
        defaultType,
    }

    public String getType() {
        return (String) getStateHelper().eval(PropertyKeys.type);
    }

    public void setType(String type) {
        getStateHelper().put(PropertyKeys.type, type);
    }

    public String getDefaultType() {
        return (String) getStateHelper().eval(PropertyKeys.defaultType);
    }

    public void setDefaultType(String defaultType) {
        getStateHelper().put(PropertyKeys.defaultType, defaultType);
    }

    private Map<String, String> getTypesComponentIdMap() {
        Map<String, String> typesComponentIdMap = (Map<String, String>) getStateHelper().get(PropertyKeys.types);
        if (null == typesComponentIdMap) {
            typesComponentIdMap = new HashMap<>();
            getStateHelper().put(PropertyKeys.types, typesComponentIdMap);
        }
        return typesComponentIdMap;
    }

    @Override
    public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
        if (event instanceof PostAddToViewEvent) {
            Map<String, String> typesComponentIdMap = getTypesComponentIdMap();
            if (!typesComponentIdMap.isEmpty()) {
                super.processEvent(event);
                return;
            }
            Map<String, UIComponent> facets = getFacets();
            for (Map.Entry<String, UIComponent> facetEntry : facets.entrySet()) {
                String type = facetEntry.getKey();
                UIComponent component = facetEntry.getValue();
                typesComponentIdMap.put(type, component.getId());
            }
            for (String type : typesComponentIdMap.keySet()) {
                UIComponent component = facets.remove(type);
                getChildren().add(component);
                component.setId(component.getId());
            }
        }
        super.processEvent(event);
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    private UIInput findInputComponent() {
        String type = getType();
        if (null == type) {
            type = getDefaultType();
        }
        Map<String, String> typesComponentIdMap = getTypesComponentIdMap();
        String componentId = typesComponentIdMap.get(type);
        if (null == componentId) {
            LOGGER.error("unknown type: {}", type);
            return null;
        }
        return (UIInput) findComponent(componentId);
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        ResponseWriter responseWriter = context.getResponseWriter();
        String clientId = super.getClientId(context);
        responseWriter.startElement("span", this);
        responseWriter.writeAttribute("id", clientId, "id");
    }

    @Override
    public void encodeChildren(FacesContext context) throws IOException {
        UIInput inputComponent = findInputComponent();
        if (null == inputComponent) {
            return;
        }
        Object value = getValue();
        inputComponent.setValue(value);
        inputComponent.encodeAll(context);
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
        ResponseWriter responseWriter = context.getResponseWriter();
        responseWriter.endElement("span");
    }

    @Override
    public void decode(FacesContext context) {
        UIInput inputComponent = findInputComponent();
        if (null == inputComponent) {
            return;
        }
        inputComponent.decode(context);
        setSubmittedValue(inputComponent.getSubmittedValue());
    }
}
