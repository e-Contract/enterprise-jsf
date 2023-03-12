/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import be.e_contract.ejsf.component.jmsinfo.RemoveEvent;
import be.e_contract.ejsf.component.jmsinfo.ReplayEvent;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named("jmsInfoController")
public class JmsInfoController {

    public void handleReplay(ReplayEvent replayEvent) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String message = "Replaying JMS message " + replayEvent.getMessageId();
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, message, null);
        facesContext.addMessage(null, facesMessage);
    }

    public void handleRemove(RemoveEvent removeEvent) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String message = "Removed JMS message " + removeEvent.getMessageId();
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, message, null);
        facesContext.addMessage(null, facesMessage);
    }
}
