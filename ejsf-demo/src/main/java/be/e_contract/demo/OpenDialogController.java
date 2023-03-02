/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("openDialogController")
public class OpenDialogController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenDialogController.class);

    public void actionWithCallbackParameter(String paramName, String paramValue) {
        LOGGER.debug("actionWithCallbackParameter: {} = {}", paramName, paramValue);
        PrimeFaces primeFaces = PrimeFaces.current();
        PrimeFaces.Ajax ajax = primeFaces.ajax();
        ajax.addCallbackParam(paramName, paramValue);
    }
}
