/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.unit.be.e_contract.ejsf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import com.yubico.webauthn.data.UserIdentity;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebAuthnTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebAuthnTest.class);

    @Test
    public void testWebAuthnLibrary() throws Exception {
        LOGGER.debug("testing registration request generation");
        RelyingPartyIdentity relyingPartyIdentity = RelyingPartyIdentity.builder().id("localhost").name("Yubico WebAuthn demo").build();
        CredentialRepository credentialRepository = new TestCredentialRepository();
        RelyingParty relyingParty = RelyingParty.builder().identity(relyingPartyIdentity).credentialRepository(credentialRepository).build();

        SecureRandom secureRandom = new SecureRandom();
        byte[] userIdData = new byte[32];
        secureRandom.nextBytes(userIdData);
        ByteArray userId = new ByteArray(userIdData);
        UserIdentity userIdentity = UserIdentity.builder().name("user name").displayName("user display name").id(userId).build();
        StartRegistrationOptions startRegistrationOptions = StartRegistrationOptions.builder().user(userIdentity).build();
        String request = relyingParty.startRegistration(startRegistrationOptions).toJson();
        LOGGER.debug("request: {}", request);
        ObjectMapper objectMapper = new ObjectMapper();
        Object jsonRequest = objectMapper.readValue(request, Object.class);
        String formattedRequest = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonRequest);
        LOGGER.debug("formatted request: {}", formattedRequest);

    }

    public static class TestCredentialRepository implements CredentialRepository {

        private static final Logger LOGGER = LoggerFactory.getLogger(TestCredentialRepository.class);

        @Override
        public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
            LOGGER.debug("getCredentialIdsForUsername: {}", username);
            return null;
        }

        @Override
        public Optional<ByteArray> getUserHandleForUsername(String username) {
            LOGGER.debug("getUserHandleForUsername: {}", username);
            return null;
        }

        @Override
        public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
            LOGGER.debug("getUsernameForUserHandle");
            return null;
        }

        @Override
        public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
            LOGGER.debug("lookup");
            return null;
        }

        @Override
        public Set<RegisteredCredential> lookupAll(ByteArray credentialId) {
            LOGGER.debug("lookupAll");
            return null;
        }

    }
}
