/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.integ.be.e_contract.ejsf;

import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import test.integ.be.e_contract.ejsf.cdi.TestScoped;

@Named
@TestScoped
public class AddMessageController {

    private String callbackParamName;

    private String callbackParamValue;

    public void addCallbackParam() {
        PrimeFaces primeFaces = PrimeFaces.current();
        primeFaces.ajax().addCallbackParam(this.callbackParamName, this.callbackParamValue);
    }

    public void setCallbackParam(String callbackParamName, String callbackParamValue) {
        this.callbackParamName = callbackParamName;
        this.callbackParamValue = callbackParamValue;
    }
}
