/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.output;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIOutput;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(OutputCertificateComponent.COMPONENT_TYPE)
public class OutputCertificateComponent extends UIOutput implements NamingContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutputCertificateComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.outputCertificate";

    public OutputCertificateComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return UINamingContainer.COMPONENT_FAMILY;
    }

    public byte[] getCertificateData() {
        X509Certificate certificate = (X509Certificate) getValue();
        if (null == certificate) {
            return null;
        }
        try {
            return certificate.getEncoded();
        } catch (CertificateEncodingException ex) {
            return null;
        }
    }

    public String getNotBeforeStyle() {
        X509Certificate certificate = (X509Certificate) getValue();
        if (null == certificate) {
            return null;
        }
        Date notBefore = certificate.getNotBefore();
        Date now = new Date();
        if (now.before(notBefore)) {
            return "color: red;";
        } else {
            return null;
        }
    }

    public String getNotAfterStyle() {
        X509Certificate certificate = (X509Certificate) getValue();
        if (null == certificate) {
            return null;
        }
        Date notAfter = certificate.getNotAfter();
        Date now = new Date();
        if (now.after(notAfter)) {
            return "color: red;";
        } else {
            return null;
        }
    }

    public StreamedContent getFile() {
        X509Certificate certificate = (X509Certificate) getValue();
        if (null == certificate) {
            return null;
        }
        InputStream certificateInputStream;
        try {
            certificateInputStream = new ByteArrayInputStream(certificate.getEncoded());
        } catch (CertificateEncodingException ex) {
            LOGGER.error("certificate encoding error: " + ex.getMessage(), ex);
            return null;
        }
        StreamedContent file
                = DefaultStreamedContent.builder()
                        .name("certificate.crt")
                        .contentType("application/pkix-cert")
                        .stream(() -> certificateInputStream)
                        .build();
        return file;
    }

    public int getKeySize() {
        X509Certificate certificate = (X509Certificate) getValue();
        if (null == certificate) {
            return 0;
        }
        PublicKey publicKey = certificate.getPublicKey();
        if (publicKey instanceof RSAPublicKey) {
            RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
            return rsaPublicKey.getModulus().bitLength();
        }
        if (publicKey instanceof ECPublicKey) {
            ECPublicKey ecPublicKey = (ECPublicKey) publicKey;
            return ecPublicKey.getParams().getCurve().getField().getFieldSize();
        }
        return 0;
    }
}
