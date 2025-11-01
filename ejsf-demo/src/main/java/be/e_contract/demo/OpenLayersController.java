/*
 * Enterprise JSF project.
 *
 * Copyright 2025 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import be.e_contract.ejsf.component.geolocation.GeolocationErrorEvent;
import be.e_contract.ejsf.component.geolocation.GeolocationPositionEvent;
import be.e_contract.ejsf.component.output.ol.LatLng;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;

@Named("openLayersController")
@ViewScoped
public class OpenLayersController implements Serializable {

    private LatLng position;

    @PostConstruct
    public void postConstruct() {
        this.position = new LatLng(44.215325, 4.468284);
    }

    public LatLng getPosition() {
        return this.position;
    }

    public void handleEvent(GeolocationPositionEvent event) {
        double latitude = event.getLatitude();
        double longitude = event.getLongitude();
        this.position = new LatLng(latitude, longitude);
    }

    public void handleErrorEvent(GeolocationErrorEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String message = "Geolocation error " + event.getError() + ": " + event.getMessage();
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null);
        facesContext.addMessage(null, facesMessage);
    }
}
