/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("menuController")
public class MenuController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuController.class);

    public String getMenuitemStyleClass(String page) {
        String viewId = getViewId();
        if (null == viewId) {
            return "";
        }
        if (!viewId.equals("/" + page + ".xhtml")) {
            return "";
        }
        return "ui-state-active";
    }

    private String getViewId() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UIViewRoot viewRoot = facesContext.getViewRoot();
        String viewId = viewRoot.getViewId();
        return viewId;
    }
}
