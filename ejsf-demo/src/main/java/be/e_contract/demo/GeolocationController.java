/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import be.e_contract.ejsf.geolocation.GeolocationAjaxBehaviorEvent;
import javax.enterprise.context.RequestScoped;
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

    public void handleEvent(GeolocationAjaxBehaviorEvent event) {
        this.latitude = event.getLatitude();
        this.longitude = event.getLongitude();
        this.accuracy = event.getAccuracy();
        LOGGER.debug("latitude: {}", this.latitude);
        LOGGER.debug("longitude: {}", this.longitude);
        LOGGER.debug("accuracy: {} meter", this.accuracy);
    }
}
