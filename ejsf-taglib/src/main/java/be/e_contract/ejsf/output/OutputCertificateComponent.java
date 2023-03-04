/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.output;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIOutput;

@FacesComponent(OutputCertificateComponent.COMPONENT_TYPE)
public class OutputCertificateComponent extends UIOutput implements NamingContainer {

    public static final String COMPONENT_TYPE = "ejsf.outputCertificate";

    public OutputCertificateComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return UINamingContainer.COMPONENT_FAMILY;
    }

    public byte[] getCertificateData() {
        X509Certificate value = (X509Certificate) getValue();
        if (null == value) {
            return null;
        }
        try {
            return value.getEncoded();
        } catch (CertificateEncodingException ex) {
            return null;
        }
    }
}
