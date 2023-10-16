/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.AuthenticatorTransport;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import com.yubico.webauthn.data.UserIdentity;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Named("webAuthnCredentialRepository")
public class WebAuthnCredentialRepository implements CredentialRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebAuthnCredentialRepository.class);

    private final Map<String, Set<Credential>> usernameCredentials = new HashMap<>();

    private static class Credential {

        private RegisteredCredential registeredCredential;
        private final Set<AuthenticatorTransport> authenticatorTransports;
        private final UserIdentity userIdentity;

        public Credential(RegisteredCredential registeredCredential, Set<AuthenticatorTransport> authenticatorTransports,
                UserIdentity userIdentity) {
            this.registeredCredential = registeredCredential;
            this.authenticatorTransports = authenticatorTransports;
            this.userIdentity = userIdentity;
        }
    }

    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
        LOGGER.debug("getCredentialIdsForUsername: {}", username);
        Set<Credential> credentials = usernameCredentials.get(username);
        if (null == credentials) {
            return Collections.EMPTY_SET;
        }
        Set<PublicKeyCredentialDescriptor> result = new HashSet<>();
        for (Credential credential : credentials) {
            RegisteredCredential registeredCredential = credential.registeredCredential;
            Set<AuthenticatorTransport> authenticatorTransports = credential.authenticatorTransports;
            PublicKeyCredentialDescriptor descriptor = PublicKeyCredentialDescriptor.builder()
                    .id(registeredCredential.getCredentialId())
                    .transports(authenticatorTransports)
                    .build();
            result.add(descriptor);
        }
        return result;
    }

    @Override
    public Optional<ByteArray> getUserHandleForUsername(String username) {
        LOGGER.debug("getUserHandleForUsername: {}", username);
        Set<Credential> credentials = this.usernameCredentials.get(username);
        if (null == credentials) {
            return Optional.empty();
        }
        for (Credential credential : credentials) {
            ByteArray userHandle = credential.userIdentity.getId();
            return Optional.of(userHandle);
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
        LOGGER.debug("getUsernameForUserHandle");
        for (Set<Credential> credentials : this.usernameCredentials.values()) {
            for (Credential credential : credentials) {
                if (credential.userIdentity.getId().equals(userHandle)) {
                    return Optional.of(credential.userIdentity.getName());
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
        LOGGER.debug("lookup");
        for (Set<Credential> credentials : this.usernameCredentials.values()) {
            for (Credential credential : credentials) {
                if (credential.registeredCredential.getCredentialId().equals(credentialId)) {
                    return Optional.of(credential.registeredCredential);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Set<RegisteredCredential> lookupAll(ByteArray credentialId) {
        LOGGER.debug("lookupAll: {}", credentialId);
        Set<RegisteredCredential> result = new HashSet<>();
        for (Set<Credential> credentials : this.usernameCredentials.values()) {
            for (Credential credential : credentials) {
                RegisteredCredential registeredCredential = credential.registeredCredential;
                if (registeredCredential.getCredentialId().equals(credentialId)) {
                    result.add(registeredCredential);
                }
            }
        }
        return result;
    }

    public void addRegistration(String username, RegisteredCredential registeredCredential, Set<AuthenticatorTransport> authenticatorTransports,
            UserIdentity userIdentity) {
        Set<Credential> credentials = this.usernameCredentials.get(username);
        if (null == credentials) {
            credentials = new HashSet<>();
            this.usernameCredentials.put(username, credentials);
        }
        Credential credential = new Credential(registeredCredential, authenticatorTransports, userIdentity);
        credentials.add(credential);
    }

    public void updateSignatureCount(AssertionResult result) {
        String username = result.getUsername();
        ByteArray credentialId = result.getCredential().getCredentialId();
        Set<Credential> credentials = this.usernameCredentials.get(username);
        if (null == credentials) {
            LOGGER.warn("unknown username: {}", username);
            return;
        }
        Credential foundCredential = null;
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
