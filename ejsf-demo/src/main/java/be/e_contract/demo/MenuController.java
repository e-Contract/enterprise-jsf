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

@Named("menuController")
public class MenuController {

    public String getMenuitemStyleClass(String page) {
        String viewId = getViewId();
        if (null == viewId) {
            return "";
        }
        if (!viewId.startsWith(page)) {
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
