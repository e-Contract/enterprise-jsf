/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import be.e_contract.ejsf.component.clocksync.ClockSyncEvent;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("clockSyncController")
@RequestScoped
public class ClockSyncController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClockSyncController.class);

    private Long bestRoundTripDelay;

    private Long deltaT;

    public void clockSynchronized(ClockSyncEvent event) {
        this.bestRoundTripDelay = event.getBestRoundTripDelay();
        this.deltaT = event.getDeltaT();
        LOGGER.debug("best round trip delay: {} ms", this.bestRoundTripDelay);
        LOGGER.debug("delta T: {} ms", this.deltaT);

        FacesContext facesContext = FacesContext.getCurrentInstance();
        String message = "Synchronized.";
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, message, null);
        facesContext.addMessage(null, facesMessage);
    }

    public Long getBestRoundTripDelay() {
        return this.bestRoundTripDelay;
    }

    public Long getDeltaT() {
        return this.deltaT;
    }
}
