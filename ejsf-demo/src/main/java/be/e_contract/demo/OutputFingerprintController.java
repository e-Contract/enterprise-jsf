/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import javax.inject.Named;

@Named("outputFingerprintController")
public class OutputFingerprintController {

    public byte[] getData() {
        return "hello world".getBytes();
    }
}
