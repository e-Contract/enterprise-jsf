/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.storage;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.el.ValueExpression;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.behavior.AjaxBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(StorageGetItemsComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "ejsf", name = "storage.js")
})
public class StorageGetItemsComponent extends UIComponentBase implements ClientBehaviorHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageGetItemsComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.storageGetItemsComponent";

    public static final String COMPONENT_FAMILY = "ejsf";

    private static final List<String> EVENT_NAMES = Collections.unmodifiableList(Arrays.asList(StorageGetItemsEvent.NAME));

    public StorageGetItemsComponent() {
        setRendererType(StorageGetItemsRenderer.RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        items,
        update
    }

    public void setUpdate(String update) {
        getStateHelper().put(PropertyKeys.update, update);
    }

    public String getUpdate() {
        return (String) getStateHelper().get(PropertyKeys.update);
    }

    public void addStorageItem(String name, String type, ValueExpression value) {
        List<StorageItem> storageItems = (List<StorageItem>) getStateHelper().get(PropertyKeys.items);
        if (null == storageItems) {
            storageItems = new LinkedList<>();
            getStateHelper().put(PropertyKeys.items, storageItems);
        }
        StorageItem storageItem = new StorageItem(name, type, value);
        storageItems.add(storageItem);
    }

    public List<StorageItem> getStorageItems() {
        List<StorageItem> storageItems = (List<StorageItem>) getStateHelper().get(PropertyKeys.items);
        if (null == storageItems) {
            return new LinkedList<>();
        }
        return storageItems;
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public String getDefaultEventName() {
        return StorageGetItemsEvent.NAME;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext facesContext = event.getFacesContext();
        ExternalContext externalContext = facesContext.getExternalContext();
        Map<String, String> requestParameterMap = externalContext.getRequestParameterMap();
        String eventName = requestParameterMap.get(ClientBehaviorContext.BEHAVIOR_EVENT_PARAM_NAME);
        if (StorageGetItemsEvent.NAME.equals(eventName)) {
            AjaxBehaviorEvent ajaxBehaviorEvent = (AjaxBehaviorEvent) event;
            AjaxBehavior ajaxBehavior = (AjaxBehavior) ajaxBehaviorEvent.getBehavior();
            StorageGetItemsEvent storageGetItemsEvent = new StorageGetItemsEvent(this, ajaxBehavior);
            List<StorageItem> storageItems = getStorageItems();
            for (StorageItem storageItem : storageItems) {
                String value = requestParameterMap.get(storageItem.getName());
                storageGetItemsEvent.addItem(storageItem.getName(), value);
            }
            storageGetItemsEvent.setPhaseId(facesContext.getCurrentPhaseId());
            super.queueEvent(storageGetItemsEvent);
            return;
        }
        super.queueEvent(event);
    }
}
