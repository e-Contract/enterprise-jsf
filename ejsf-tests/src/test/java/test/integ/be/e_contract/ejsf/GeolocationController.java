/*
 * Enterprise JSF project.
 *
 * Copyright 2023-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.integ.be.e_contract.ejsf;

import be.e_contract.ejsf.component.geolocation.GeolocationErrorEvent;
import be.e_contract.ejsf.component.geolocation.GeolocationPositionEvent;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.integ.be.e_contract.ejsf.cdi.TestScoped;

@Named("geolocationController")
@TestScoped
public class GeolocationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeolocationController.class);

    private Double latitude;

    private Double longitude;

    private Double accuracy;

    public Double getLatitude() {
        return this.latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Double getAccuracy() {
        return this.accuracy;
    }

    public void handleEvent(GeolocationPositionEvent event) {
        this.latitude = event.getLatitude();
        this.longitude = event.getLongitude();
        this.accuracy = event.getAccuracy();
        LOGGER.debug("latitude: {}", this.latitude);
        LOGGER.debug("longitude: {}", this.longitude);
        LOGGER.debug("accuracy: {} meter", this.accuracy);
    }

    public void handleErrorEvent(GeolocationErrorEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String message = "Geolocation error " + event.getError() + ": " + event.getMessage();
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null);
        facesContext.addMessage(null, facesMessage);
    }
}
