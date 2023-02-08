/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.validator.ratelimiter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RateInfo {

    private static final Logger LOGGER = LoggerFactory.getLogger(RateInfo.class);

    private int tries;

    public RateInfo() {
        LOGGER.debug("constructor");
        this.tries = 5;
    }

    public boolean reachedLimit() {
        this.tries--;
        return this.tries <= 0;
    }
}
