/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
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
        space
    }

    public boolean isSpace() {
        return (Boolean) getStateHelper().eval(PropertyKeys.space, false);
    }

    public void setSpace(boolean space) {
        getStateHelper().put(PropertyKeys.space, space);
    }

    public void setAlgo(String algo) {
        getStateHelper().put(PropertyKeys.algo, algo);
    }

    public String getAlgo() {
        return (String) getStateHelper().eval(PropertyKeys.algo);
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
        ResponseWriter responseWriter = context.getResponseWriter();

        String clientId = super.getClientId(context);
        responseWriter.startElement("span", this);
        responseWriter.writeAttribute("id", clientId, "id");

        byte[] data = (byte[]) getValue();
        if (null != data) {
            String algo = getAlgo();
            if (null == algo) {
                algo = "SHA1";
            }
            MessageDigest messageDigest;
            try {
                messageDigest = MessageDigest.getInstance(algo);
            } catch (NoSuchAlgorithmException ex) {
                LOGGER.warn("unsupported algo: {}", algo);
                return;
            }
            byte[] digestValue = messageDigest.digest(data);
            String fingerprint = Hex.encodeHexString(digestValue).toUpperCase();
            boolean space = isSpace();
            if (space) {
                for (int idx = 0; idx < fingerprint.length(); idx += 2) {
                    responseWriter.write(fingerprint.charAt(idx));
                    responseWriter.write(fingerprint.charAt(idx + 1));
                    responseWriter.write(" ");
                }
            } else {
                responseWriter.write(fingerprint);
            }
        }

        responseWriter.endElement("span");
    }
}
