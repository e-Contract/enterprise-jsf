/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.AuthenticatorTransport;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import com.yubico.webauthn.data.UserIdentity;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Named("webAuthnCredentialRepository")
public class WebAuthnCredentialRepository implements CredentialRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebAuthnCredentialRepository.class);

    // username, List of JSON encoded Credentials
    private final Map<String, Set<String>> usernameCredentials = new HashMap<>();

    private static class Credential {

        @JsonProperty("registeredCredential")
        private RegisteredCredential registeredCredential;

        @JsonProperty("authenticatorTransports")
        private Set<AuthenticatorTransport> authenticatorTransports;

        @JsonProperty("userIdentity")
        private UserIdentity userIdentity;

        public Credential() {
            super();
        }

        public Credential(RegisteredCredential registeredCredential, Set<AuthenticatorTransport> authenticatorTransports,
                UserIdentity userIdentity) {
            this.registeredCredential = registeredCredential;
            this.authenticatorTransports = authenticatorTransports;
            this.userIdentity = userIdentity;
        }

        private static ObjectMapper createObjectMapper() {
            ObjectMapper objectMapper = JsonMapper.builder()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
                    .serializationInclusion(JsonInclude.Include.NON_ABSENT)
                    .defaultBase64Variant(Base64Variants.MODIFIED_FOR_URL)
                    .addModule(new Jdk8Module())
                    .addModule(new JavaTimeModule())
                    .build();
            return objectMapper;
        }

        public String toJSON() {
            ObjectMapper objectMapper = createObjectMapper();
            StringWriter stringWriter = new StringWriter();
            try {
                objectMapper.writeValue(stringWriter, this);
            } catch (IOException ex) {
                LOGGER.error("I/O error: " + ex.getMessage(), ex);
            }
            LOGGER.debug("JSON credential: {}", stringWriter);
            LOGGER.debug("JSON credential size: {} bytes", stringWriter.toString().length());
            return stringWriter.toString();
        }

        public static Credential fromJSON(String json) {
            ObjectMapper objectMapper = createObjectMapper();
            try {
                return objectMapper.readValue(json, Credential.class);
            } catch (JsonProcessingException ex) {
                LOGGER.error("JSON error: " + ex.getMessage(), ex);
            }
            return null;
        }

        public static Set<Credential> fromJSON(Set<String> jsonCredentials) {
            Set<Credential> credentials = new HashSet<>();
            for (String jsonCredential : jsonCredentials) {
                Credential credential = Credential.fromJSON(jsonCredential);
                credentials.add(credential);
            }
            return credentials;
        }
    }

    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
        LOGGER.debug("getCredentialIdsForUsername: {}", username);
        Set<String> jsonCredentials = usernameCredentials.get(username);
        if (null == jsonCredentials) {
            return Collections.EMPTY_SET;
        }
        Set<Credential> credentials = Credential.fromJSON(jsonCredentials);
        Set<PublicKeyCredentialDescriptor> result = new HashSet<>();
        for (Credential credential : credentials) {
            RegisteredCredential registeredCredential = credential.registeredCredential;
            ByteArray credentialId = registeredCredential.getCredentialId();
            LOGGER.debug("credential id: {}", credentialId.getHex());
            Set<AuthenticatorTransport> authenticatorTransports = credential.authenticatorTransports;
            PublicKeyCredentialDescriptor descriptor = PublicKeyCredentialDescriptor.builder()
                    .id(credentialId)
                    .transports(authenticatorTransports)
                    .build();
            result.add(descriptor);
        }
        return result;
    }

    @Override
    public Optional<ByteArray> getUserHandleForUsername(String username) {
        LOGGER.debug("getUserHandleForUsername: {}", username);
        Set<String> jsonCredentials = this.usernameCredentials.get(username);
        if (null == jsonCredentials) {
            return Optional.empty();
        }
        Set<Credential> credentials = Credential.fromJSON(jsonCredentials);
        for (Credential credential : credentials) {
            ByteArray userHandle = credential.userIdentity.getId();
            LOGGER.debug("user handle: {}", userHandle.getHex());
            return Optional.of(userHandle);
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
        LOGGER.debug("getUsernameForUserHandle: {}", userHandle.getHex());
        Collection<Set<String>> allJsonCredentials = this.usernameCredentials.values();
        for (Set<String> jsonCredentials : allJsonCredentials) {
            Set<Credential> credentials = Credential.fromJSON(jsonCredentials);
            for (Credential credential : credentials) {
                if (credential.userIdentity.getId().equals(userHandle)) {
                    String username = credential.userIdentity.getName();
                    LOGGER.debug("username: {}", username);
                    return Optional.of(username);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
        LOGGER.debug("lookup");
        LOGGER.debug("credential id: {}", credentialId.getHex());
        LOGGER.debug("user handle: {}", userHandle.getHex());
        Collection<Set<String>> allJsonCredentials = this.usernameCredentials.values();
        for (Set<String> jsonCredentials : allJsonCredentials) {
            Set<Credential> credentials = Credential.fromJSON(jsonCredentials);
            for (Credential credential : credentials) {
                if (credential.registeredCredential.getCredentialId().equals(credentialId)) {
                    if (credential.registeredCredential.getUserHandle().equals(userHandle)) {
                        RegisteredCredential registeredCredential
                                = RegisteredCredential.builder()
                                        .credentialId(credential.registeredCredential.getCredentialId())
                                        .userHandle(credential.userIdentity.getId())
                                        .publicKeyCose(credential.registeredCredential.getPublicKeyCose())
                                        .signatureCount(credential.registeredCredential.getSignatureCount())
                                        .build();
                        return Optional.of(registeredCredential);
                    }
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Set<RegisteredCredential> lookupAll(ByteArray credentialId) {
        LOGGER.debug("lookupAll: {}", credentialId.getHex());
        Set<RegisteredCredential> result = new HashSet<>();
        Collection<Set<String>> allJsonCredentials = this.usernameCredentials.values();
        for (Set<String> jsonCredentials : allJsonCredentials) {
            Set<Credential> credentials = Credential.fromJSON(jsonCredentials);
            for (Credential credential : credentials) {
                if (credential.registeredCredential.getCredentialId().equals(credentialId)) {
                    RegisteredCredential registeredCredential = RegisteredCredential.builder()
                            .credentialId(credential.registeredCredential.getCredentialId())
                            .userHandle(credential.userIdentity.getId())
                            .publicKeyCose(credential.registeredCredential.getPublicKeyCose())
                            .signatureCount(credential.registeredCredential.getSignatureCount())
                            .build();
                    result.add(registeredCredential);
                }
            }
        }
        return result;
    }

    public void addRegistration(String username, RegisteredCredential registeredCredential, Set<AuthenticatorTransport> authenticatorTransports,
            UserIdentity userIdentity) {
        LOGGER.debug("add registration for user {}", username);
        LOGGER.debug("user id: {}", userIdentity.getId().getHex());
        LOGGER.debug("user handle: {}", registeredCredential.getUserHandle().getHex());
        LOGGER.debug("credential id: {}", registeredCredential.getCredentialId().getHex());
        Set<String> jsoncredentials = this.usernameCredentials.get(username);
        if (null == jsoncredentials) {
            jsoncredentials = new HashSet<>();
            this.usernameCredentials.put(username, jsoncredentials);
        }
        Credential credential = new Credential(registeredCredential, authenticatorTransports, userIdentity);

        jsoncredentials.add(credential.toJSON());
    }

    public void updateSignatureCount(AssertionResult result) {
        String username = result.getUsername();
        ByteArray credentialId = result.getCredential().getCredentialId();
        Set<String> jsonCredentials = this.usernameCredentials.get(username);
        if (null == jsonCredentials) {
            LOGGER.warn("unknown username: {}", username);
            return;
        }
        Credential foundCredential = null;
        Set<Credential> credentials = Credential.fromJSON(jsonCredentials);
        for (Credential credential : credentials) {
            RegisteredCredential registeredCredential = credential.registeredCredential;
            if (registeredCredential.getCredentialId().equals(credentialId)) {
                foundCredential = credential;
                break;
            }
        }
        if (null == foundCredential) {
            LOGGER.warn("unknown credential {} for user {}", credentialId.getHex(), username);
            return;
        }
        foundCredential.registeredCredential = foundCredential.registeredCredential
                .toBuilder()
                .signatureCount(result.getSignatureCount())
                .build();
    }

    public Set<String> getUsers() {
        return this.usernameCredentials.keySet();
    }

    public void removeAll() {
        this.usernameCredentials.clear();
    }

    public void remove(String username) {
        this.usernameCredentials.remove(username);
    }
}
