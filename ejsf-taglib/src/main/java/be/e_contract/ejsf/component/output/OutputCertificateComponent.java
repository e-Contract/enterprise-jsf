/*
 * Enterprise JSF project.
 *
 * Copyright 2023-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.output;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.faces.application.Application;
import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.convert.DateTimeConverter;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ListenerFor;
import javax.faces.event.PostAddToViewEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(OutputCertificateComponent.COMPONENT_TYPE)
@ListenerFor(systemEventClass = PostAddToViewEvent.class)
public class OutputCertificateComponent extends UIOutput implements NamingContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutputCertificateComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.outputCertificate";

    private UIOutput notBefore;

    private UIOutput notAfter;

    public OutputCertificateComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return UINamingContainer.COMPONENT_FAMILY;
    }

    public UIOutput getNotBefore() {
        return this.notBefore;
    }

    public void setNotBefore(UIOutput notBefore) {
        this.notBefore = notBefore;
    }

    public UIOutput getNotAfter() {
        return this.notAfter;
    }

    public void setNotAfter(UIOutput notAfter) {
        this.notAfter = notAfter;
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

    public long getDaysLeft() {
        X509Certificate certificate = (X509Certificate) getValue();
        if (null == certificate) {
            return 0;
        }
        Date now = new Date();
        Date notAfter = certificate.getNotAfter();
        long diffInMillies = notAfter.getTime() - now.getTime();
        if (diffInMillies < 0) {
            diffInMillies = 0;
        }
        long days = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        return days;
    }

    public String getSerialNumber() {
        X509Certificate certificate = (X509Certificate) getValue();
        if (null == certificate) {
            return "";
        }
        BigInteger serialNumber = certificate.getSerialNumber();
        return serialNumber.toString(16).toUpperCase();
    }

    @Override
    public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
        if (event instanceof PostAddToViewEvent) {
            String dateTimePattern = (String) getAttributes().get("dateTimePattern");
            if (null != dateTimePattern) {
                FacesContext facesContext = event.getFacesContext();
                Application application = facesContext.getApplication();
                DateTimeConverter dateTimeConverter = (DateTimeConverter) application.createConverter(DateTimeConverter.CONVERTER_ID);
                dateTimeConverter.setType("both");
                dateTimeConverter.setPattern(dateTimePattern);
                this.notBefore.setConverter(dateTimeConverter);
                this.notAfter.setConverter(dateTimeConverter);
            }
        }
        super.processEvent(event);
    }
}
