/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.viewlogs;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizationCode {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationCode.class);

    private static final String SECRET_KEY_ATTRIBUTE = AuthorizationCode.class.getName() + ".secretKey";

    private final FacesContext facesContext;

    private final Mac mac;

    public AuthorizationCode(FacesContext facesContext) {
        this.facesContext = facesContext;
        byte[] secretKey = getSecretKey();
        String algo = "HmacSHA1";
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, algo);
        try {
            this.mac = Mac.getInstance(algo);
        } catch (NoSuchAlgorithmException ex) {
            LOGGER.error("algo not available: " + ex.getMessage(), ex);
            throw new RuntimeException("algo not available: " + ex.getMessage(), ex);
        }
        try {
            this.mac.init(secretKeySpec);
        } catch (InvalidKeyException ex) {
            LOGGER.error("invalid key: " + ex.getMessage(), ex);
            throw new RuntimeException("invalid key");
        }
    }

    public String generateCode(String identifier) {
        ExternalContext externalContext = this.facesContext.getExternalContext();
        HttpSession httpSession = (HttpSession) externalContext.getSession(true);
        String sessionId = httpSession.getId();
        this.mac.reset();
        this.mac.update(sessionId.getBytes());
        byte[] code = this.mac.doFinal(identifier.getBytes());
        return Hex.encodeHexString(code);
    }

    public boolean validateCode(String identifier, String code) {
        String expectedCode = generateCode(identifier);
        return expectedCode.equals(code);
    }

    private byte[] getSecretKey() {
        ExternalContext externalContext = this.facesContext.getExternalContext();
        ServletContext servletContext = (ServletContext) externalContext.getContext();
        byte[] secretKey = (byte[]) servletContext.getAttribute(SECRET_KEY_ATTRIBUTE);
        if (null != secretKey) {
            return secretKey;
        }
        SecureRandom secureRandom;
        try {
            secureRandom = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException ex) {
            LOGGER.error("algo not available: " + ex.getMessage(), ex);
            return null;
        }
        secureRandom.setSeed(System.currentTimeMillis());
        secretKey = new byte[20];
        secureRandom.nextBytes(secretKey);
        servletContext.setAttribute(SECRET_KEY_ATTRIBUTE, secretKey);
        return secretKey;
    }
}
