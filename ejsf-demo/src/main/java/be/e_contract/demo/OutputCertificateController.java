/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.inject.Named;

@Named("outputCertificateController")
public class OutputCertificateController {

    public X509Certificate getCertificate() {
        InputStream certificateInputStream = OutputCertificateController.class.getResourceAsStream("/demo.crt");
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
            X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(certificateInputStream);
            return certificate;
        } catch (CertificateException ex) {
            return null;
        }
    }
}
