/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import be.e_contract.ejsf.geolocation.GeolocationEvent;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("geolocationController")
public class GeolocationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeolocationController.class);

    public void handleEvent(GeolocationEvent event) {
        LOGGER.debug("latitude: {}", event.getLatitude());
        LOGGER.debug("longitude: {}", event.getLongitude());
        LOGGER.debug("accuracy: {} meter", event.getAccuracy());
    }
}
