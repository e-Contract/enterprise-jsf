package be.e_contract.jsf.taglib.extension;

import java.util.List;
import javax.faces.application.Application;
import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtensionViewHandlerWrapper extends ViewHandlerWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionViewHandlerWrapper.class);

    private static final String EXT_RES_COMP_ID = "ejsf-extension-resource";

    public ExtensionViewHandlerWrapper(ViewHandler wrapped) {
        super(wrapped);
    }

    @Override
    public UIViewRoot createView(FacesContext context, String viewId) {
        LOGGER.debug("createView: {}", viewId);
        UIViewRoot viewRoot = super.createView(context, viewId);
        extend(context, viewRoot);
        return viewRoot;
    }

    @Override
    public UIViewRoot restoreView(FacesContext context, String viewId) {
        LOGGER.debug("restoreView: {}", viewId);
        UIViewRoot viewRoot = super.restoreView(context, viewId);
        extend(context, viewRoot);
        return viewRoot;
    }

    private void extend(FacesContext context, UIViewRoot viewRoot) {
        List<UIComponent> componentResources = viewRoot.getComponentResources(context, "head");
        for (UIComponent componentResource : componentResources) {
            if (EXT_RES_COMP_ID.equals(componentResource.getId())) {
                return;
            }
        }
        Application application = context.getApplication();
        UIComponent componentResource = application.createComponent(UIOutput.COMPONENT_TYPE);
        componentResource.setRendererType("javax.faces.resource.Script");
        componentResource.getAttributes().put("library", "ejsf");
        componentResource.getAttributes().put("name", "extension.js");
        componentResource.setId(EXT_RES_COMP_ID);
        viewRoot.addComponentResource(context, componentResource, "head");
    }
}
