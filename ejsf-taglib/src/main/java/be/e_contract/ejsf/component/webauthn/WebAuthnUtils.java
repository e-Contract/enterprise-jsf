/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.webauthn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WebAuthn Utility class. This prevents JSF class loading errors when WebAuthn
 * component dependencies are not available at runtime.
 *
 * @author Frank Cornelis
 */
public class WebAuthnUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebAuthnUtils.class);

    public static String toJson(PublicKeyCredentialCreationOptions publicKeyCredentialCreationOptions) {
        String json;
        try {
            json = publicKeyCredentialCreationOptions.toJson();
            LOGGER.debug("request: {}", json);
        } catch (JsonProcessingException ex) {
            LOGGER.error("JSON error: " + ex.getMessage(), ex);
            return null;
        }
        return json;
    }

    public static String toJson(AssertionRequest assertionRequest) {
        String json;
        try {
            json = assertionRequest.toJson();
        } catch (JsonProcessingException ex) {
            LOGGER.error("JSON error: " + ex.getMessage(), ex);
            return null;
        }
        return json;
    }

    public static String toCredentialsJson(AssertionRequest assertionRequest) {
        String json;
        try {
            json = assertionRequest.toCredentialsGetJson();
        } catch (JsonProcessingException ex) {
            LOGGER.error("JSON error: " + ex.getMessage(), ex);
            return null;
        }
        return json;
    }
}
