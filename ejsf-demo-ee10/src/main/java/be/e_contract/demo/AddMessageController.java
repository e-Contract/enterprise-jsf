/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("addMessageController")
public class AddMessageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddMessageController.class);

    public void addCallbackParam() {
        LOGGER.debug("addCallbackParam invoked");
        PrimeFaces primeFaces = PrimeFaces.current();
        primeFaces.ajax().addCallbackParam("callbackParam", "some value");
    }
}
