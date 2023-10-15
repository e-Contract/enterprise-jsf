/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.webauthn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.exception.Base64UrlException;
import java.security.SecureRandom;
import java.util.function.Function;
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

    public static JsonNode toJsonNode(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(json);
        } catch (JsonProcessingException ex) {
            LOGGER.error("JSON error: " + ex.getMessage(), ex);
            return null;
        }
        return jsonNode;
    }

    public static String addAuthenticationPRF(String publicKeyCredentialRequestOptions,
            Function<ByteArray, ByteArray> salter) {
        JsonNode jsonNode = toJsonNode(publicKeyCredentialRequestOptions);
        ObjectNode extensions = (ObjectNode) jsonNode.findValue("extensions");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode prf = objectMapper.createObjectNode();
        extensions.set("prf", prf);
        ObjectNode evalByCredential = objectMapper.createObjectNode();
        prf.set("evalByCredential", evalByCredential);
        ArrayNode allowCredentials = (ArrayNode) jsonNode.findValue("allowCredentials");
        for (JsonNode allowCredential : allowCredentials) {
            String credentialId = allowCredential.findValue("id").asText();
            ByteArray credentialIdByteArray;
            try {
                credentialIdByteArray = ByteArray.fromBase64Url(credentialId);
            } catch (Base64UrlException ex) {
                LOGGER.error("base64 error: " + ex.getMessage(), ex);
                return null;
            }
            ByteArray saltByteArray = salter.apply(credentialIdByteArray);
            ObjectNode prfValues = objectMapper.createObjectNode();
            prfValues.put("first", saltByteArray.getBase64Url());
            evalByCredential.set(credentialId, prfValues);
        }
        try {
            return objectMapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException ex) {
            LOGGER.error("JSON error: " + ex.getMessage(), ex);
            return null;
        }
    }

    public static String addRegistrationPRF(String publicKeyCredentialCreationOptions) {
        JsonNode jsonNode = toJsonNode(publicKeyCredentialCreationOptions);
        ObjectNode extensions = (ObjectNode) jsonNode.findValue("extensions");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode prf = objectMapper.createObjectNode();
        extensions.set("prf", prf);
        ObjectNode eval = objectMapper.createObjectNode();
        prf.set("eval", eval);
        byte[] salt = new byte[32];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);
        ByteArray saltByteArray = new ByteArray(salt);
        eval.put("first", saltByteArray.getBase64Url());
        try {
            return objectMapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException ex) {
            LOGGER.error("JSON error: " + ex.getMessage(), ex);
            return null;
        }
    }

    public static Boolean hasPRF(String createResponse) {
        JsonNode jsonNode = toJsonNode(createResponse);
        JsonNode clientExtensionResultsJsonNode = jsonNode.get("clientExtensionResults");
        if (null == clientExtensionResultsJsonNode) {
            return null;
        }
        JsonNode prfJsonNode = clientExtensionResultsJsonNode.get("prf");
        if (null == prfJsonNode) {
            return null;
        }
        JsonNode enabledJsonNode = prfJsonNode.get("enabled");
        if (null == enabledJsonNode) {
            return null;
        }
        return enabledJsonNode.asBoolean();
    }

    public static ByteArray getPRFResults(String assertionResponse) {
        JsonNode jsonNode = toJsonNode(assertionResponse);
        JsonNode clientExtensionResultsJsonNode = jsonNode.get("clientExtensionResults");
        if (null == clientExtensionResultsJsonNode) {
            return null;
        }
        JsonNode prfJsonNode = clientExtensionResultsJsonNode.get("prf");
        if (null == prfJsonNode) {
            return null;
        }
        JsonNode resultsJsonNode = prfJsonNode.get("results");
        if (null == resultsJsonNode) {
            return null;
        }
        JsonNode firstJsonNode = resultsJsonNode.get("first");
        if (null == firstJsonNode) {
            return null;
        }
        try {
            return ByteArray.fromBase64Url(firstJsonNode.asText());
        } catch (Base64UrlException ex) {
            LOGGER.error("base64 error: " + ex.getMessage(), ex);
            return null;
        }
    }
}
