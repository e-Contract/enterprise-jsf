/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import be.e_contract.ejsf.output.visnetwork.DoubleClickEvent;
import java.io.IOException;
import java.io.InputStream;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.apache.commons.io.IOUtils;

@Named("visNetworkController")
public class VisNetworkController {

    public String getValue() {
        InputStream inputStream
                = VisNetworkController.class.getResourceAsStream("/vis-network.json");
        try {
            return IOUtils.toString(inputStream, "UTF-8");
        } catch (IOException ex) {
            return "";
        }
    }

    public void handleDoubleClick(DoubleClickEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String message = "Double clicked " + event.getNode();
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, message, null);
        facesContext.addMessage(null, facesMessage);
    }
}
