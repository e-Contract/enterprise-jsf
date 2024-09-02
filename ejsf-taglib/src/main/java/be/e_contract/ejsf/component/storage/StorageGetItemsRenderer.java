/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.storage;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.el.ELContext;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.AjaxBehavior;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.component.search.SearchExpressionContext;
import javax.faces.component.search.SearchExpressionHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;
import org.primefaces.util.ComponentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesRenderer(componentFamily = StorageGetItemsComponent.COMPONENT_FAMILY, rendererType = StorageGetItemsRenderer.RENDERER_TYPE)
public class StorageGetItemsRenderer extends Renderer {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageGetItemsRenderer.class);

    public static final String RENDERER_TYPE = "ejsf.storageGetItemsRenderer";

    @Override
    public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
        StorageGetItemsComponent storageGetItemsComponent = (StorageGetItemsComponent) component;
        String clientId = storageGetItemsComponent.getClientId();
        ResponseWriter responseWriter = facesContext.getResponseWriter();

        responseWriter.startElement("span", component);
        responseWriter.writeAttribute("id", clientId, "id");
        String param = "";
        List<StorageItem> storageItems = storageGetItemsComponent.getStorageItems();
        for (StorageItem storageItem : storageItems) {
            if (!param.isEmpty()) {
                param += ",";
            }
            param += storageItem.getName() + ":" + storageItem.getType();
        }
        responseWriter.writeAttribute("data-ejsf-storage-get-items", param, null);
        Map<String, List<ClientBehavior>> clientBehaviorMap = storageGetItemsComponent.getClientBehaviors();
        List<ClientBehavior> clientBehaviors = clientBehaviorMap.get(StorageGetItemsEvent.NAME);
        Application application = facesContext.getApplication();
        SearchExpressionHandler searchExpressionHandler = application.getSearchExpressionHandler();
        SearchExpressionContext searchExpressionContext = SearchExpressionContext.createSearchExpressionContext(facesContext, component);
        Set<String> renderClientIds = new HashSet<>();
        if (null != clientBehaviors) {
            for (ClientBehavior clientBehavior : clientBehaviors) {
                if (clientBehavior instanceof AjaxBehavior) {
                    AjaxBehavior ajaxBehavior = (AjaxBehavior) clientBehavior;
                    Collection<String> renderCollection = ajaxBehavior.getRender();
                    for (String render : renderCollection) {
                        List<String> clientIds = searchExpressionHandler.resolveClientIds(searchExpressionContext, render);
                        renderClientIds.addAll(clientIds);
                    }
                }
            }
        }
        String update = storageGetItemsComponent.getUpdate();
        if (null != update) {
            List<String> clientIds = searchExpressionHandler.resolveClientIds(searchExpressionContext, update);
            renderClientIds.addAll(clientIds);
        }
        String renderValue = "";
        for (String renderClientId : renderClientIds) {
            if (!renderValue.isEmpty()) {
                renderValue += ",";
            }
            renderValue += renderClientId;
        }
        responseWriter.writeAttribute("data-ejsf-storage-get-items-render", renderValue, null);
    }

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        ResponseWriter responseWriter = facesContext.getResponseWriter();
        responseWriter.endElement("span");
    }

    @Override
    public void decode(FacesContext facesContext, UIComponent component) {
        ExternalContext externalContext = facesContext.getExternalContext();
        Map<String, String> params = externalContext.getRequestParameterMap();
        String behaviorSource = params.get(ClientBehaviorContext.BEHAVIOR_SOURCE_PARAM_NAME);
        if (null == behaviorSource) {
            return;
        }
        String clientId = component.getClientId(facesContext);
        if (!behaviorSource.equals(clientId)) {
            return;
        }
        String behaviorEvent = params.get(ClientBehaviorContext.BEHAVIOR_EVENT_PARAM_NAME);
        if (null == behaviorEvent) {
            return;
        }
        if (StorageGetItemsEvent.NAME.equals(behaviorEvent)) {
            LOGGER.debug("items received");
            StorageGetItemsComponent storageGetItemsComponent = (StorageGetItemsComponent) component;
            List<StorageItem> storageItems = storageGetItemsComponent.getStorageItems();
            for (StorageItem storageItem : storageItems) {
                String value = params.get(storageItem.getName());
                LOGGER.debug("item {} = {}", storageItem.getName(), value);
                ELContext elContext = facesContext.getELContext();
                storageItem.getValue().setValue(elContext, value);
            }
        }
        ComponentUtils.decodeBehaviors(facesContext, component);
    }
}
