/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import be.e_contract.ejsf.component.performance.PerformanceNavigationTimingEvent;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named("performanceNavigationController")
public class PerformanceNavigationController {

    public void handleEvent(PerformanceNavigationTimingEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "start time: " + event.getStartTime(), null));
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "duration: " + event.getDuration(), null));
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "DOM interactive: " + event.getDomInteractive(), null));
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "DOM complete: " + event.getDomComplete(), null));
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "load event start: " + event.getLoadEventStart(), null));
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "load event end: " + event.getLoadEventEnd(), null));
    }
}
