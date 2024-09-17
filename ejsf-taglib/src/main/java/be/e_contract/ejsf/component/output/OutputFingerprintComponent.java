/*
 * Enterprise JSF project.
 *
 * Copyright 2023-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.output;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(OutputFingerprintComponent.COMPONENT_TYPE)
public class OutputFingerprintComponent extends UIOutput {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutputFingerprintComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.outputFingerprint";

    public static final String COMPONENT_FAMILY = "ejsf";

    public OutputFingerprintComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        algo,
        spacing,
        separator,
        lowercase,
    }

    public boolean isSpacing() {
        return (Boolean) getStateHelper().eval(PropertyKeys.spacing, false);
    }

    public void setSpacing(boolean spacing) {
        getStateHelper().put(PropertyKeys.spacing, spacing);
    }

    public void setAlgo(String algo) {
        getStateHelper().put(PropertyKeys.algo, algo);
    }

    public String getAlgo() {
        return (String) getStateHelper().eval(PropertyKeys.algo, "SHA-1");
    }

    public void setSeparator(String separator) {
        getStateHelper().put(PropertyKeys.separator, separator);
    }

    public String getSeparator() {
        return (String) getStateHelper().eval(PropertyKeys.separator, " ");
    }

    public boolean isLowercase() {
        return (Boolean) getStateHelper().eval(PropertyKeys.lowercase, false);
    }

    public void setLowercase(boolean lowercase) {
        getStateHelper().put(PropertyKeys.lowercase, lowercase);
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        if (!isRendered()) {
            return;
        }
        ResponseWriter responseWriter = context.getResponseWriter();

        String clientId = super.getClientId(context);
        responseWriter.startElement("span", this);
        responseWriter.writeAttribute("id", clientId, "id");

        byte[] data = (byte[]) getValue();
        if (null != data) {
            String algo = getAlgo();
            MessageDigest messageDigest;
            try {
                messageDigest = MessageDigest.getInstance(algo);
            } catch (NoSuchAlgorithmException ex) {
                LOGGER.warn("unsupported algo: {}", algo);
                return;
            }
            byte[] digestValue = messageDigest.digest(data);
            String fingerprint = Hex.encodeHexString(digestValue);
            boolean lowercase = isLowercase();
            if (!lowercase) {
                fingerprint = fingerprint.toUpperCase();
            }
            boolean spacing = isSpacing();
            if (spacing) {
                String separator = getSeparator();
                int length = fingerprint.length();
                for (int idx = 0; idx < length; idx += 2) {
                    responseWriter.write(fingerprint.charAt(idx));
                    responseWriter.write(fingerprint.charAt(idx + 1));
                    if (idx + 2 < length) {
                        responseWriter.write(separator);
                    }
                }
            } else {
                responseWriter.write(fingerprint);
            }
        }

        responseWriter.endElement("span");
    }
}
