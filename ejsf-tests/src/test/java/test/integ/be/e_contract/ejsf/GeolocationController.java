/*
 * Enterprise JSF project.
 *
 * Copyright 2023-2025 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.integ.be.e_contract.ejsf;

import be.e_contract.ejsf.component.geolocation.GeolocationErrorEvent;
import be.e_contract.ejsf.component.geolocation.GeolocationPositionError;
import be.e_contract.ejsf.component.geolocation.GeolocationPositionEvent;
import jakarta.annotation.PostConstruct;
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

    private GeolocationPositionError error;

    private boolean received;

    @PostConstruct
    public void postConstruct() {
        reset();
    }

    public void reset() {
        this.received = false;
        this.latitude = null;
        this.longitude = null;
        this.accuracy = null;
        this.error = null;
    }

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
        this.received = true;
    }

    public void handleErrorEvent(GeolocationErrorEvent event) {
        LOGGER.debug("error code: {}", event.getError());
        LOGGER.debug("error message: {}", event.getMessage());
        this.error = event.getError();
        this.received = true;
    }

    public GeolocationPositionError getError() {
        return this.error;
    }

    public boolean isReceived() {
        return this.received;
    }
}
