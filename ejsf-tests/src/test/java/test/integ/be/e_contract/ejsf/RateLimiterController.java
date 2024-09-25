/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.integ.be.e_contract.ejsf;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.integ.be.e_contract.ejsf.cdi.TestScoped;

@Named
@TestScoped
public class RateLimiterController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimiterController.class);

    private String username;

    @PostConstruct
    public void postConstruct() {
        this.username = null;
    }

    public void onLimit(String username) {
        LOGGER.debug("onLimit: {}", username);
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }
}
