/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JSF Utility Functions. Check out OmniFaces before adding functions here.
 *
 * @author Frank Cornelis
 * @see <a href="https://github.com/omnifaces/omnifaces">OmniFaces</a>
 */
public class Utils {

    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    public static void invalidateInput(String... relativeIds) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UIComponent currentComponent = UIComponent.getCurrentComponent(facesContext);
        if (null == currentComponent) {
            LOGGER.error("no current component");
            return;
        }
        for (String relativeId : relativeIds) {
            UIComponent component = currentComponent.findComponent(relativeId);
            if (null == component) {
                LOGGER.error("component not found: {}", relativeId);
                continue;
            }
            if (!(component instanceof UIInput)) {
                LOGGER.error("component is not a UIInput: {}", component.getId());
                continue;
            }
            UIInput inputComponent = (UIInput) component;
            inputComponent.setValid(false);
        }
    }

    public static void invalidateInput(FacesMessage facesMessage, String relativeId) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UIComponent currentComponent = UIComponent.getCurrentComponent(facesContext);
        if (null == currentComponent) {
            LOGGER.debug("no current component, using view root");
            currentComponent = facesContext.getViewRoot();
        }
        UIComponent component = currentComponent.findComponent(relativeId);
        if (null == component) {
            LOGGER.error("component not found: {}", relativeId);
            facesContext.addMessage(null, facesMessage);
            return;
        }
        String clientId = component.getClientId();
        facesContext.addMessage(clientId, facesMessage);
        if (!(component instanceof UIInput)) {
            LOGGER.error("component is not a UIInput: {}", component.getId());
            return;
        }
        UIInput inputComponent = (UIInput) component;
        inputComponent.setValid(false);
    }
}
