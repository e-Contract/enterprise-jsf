/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import be.e_contract.ejsf.geolocation.GeolocationErrorEvent;
import be.e_contract.ejsf.geolocation.GeolocationPositionEvent;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("geolocationController")
@RequestScoped
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
