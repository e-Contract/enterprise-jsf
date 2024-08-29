/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import java.util.concurrent.ThreadLocalRandom;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("asyncDemoController")
public class AsyncDemoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncDemoController.class);

    public String getValue() {
        int delay = ThreadLocalRandom.current().nextInt(1000, 5 * 1000);
        LOGGER.debug("sleeping for {} ms", delay);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ex) {
            LOGGER.error("sleep error: " + ex.getMessage(), ex);
        }
        return "hello world after " + delay + " ms";
    }
}
