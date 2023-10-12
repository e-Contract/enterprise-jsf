/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.unit.be.e_contract.ejsf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webauthn4j.converter.util.JsonConverter;
import com.webauthn4j.converter.util.ObjectConverter;
import com.webauthn4j.data.PublicKeyCredentialCreationOptions;
import com.webauthn4j.data.PublicKeyCredentialParameters;
import com.webauthn4j.data.PublicKeyCredentialRpEntity;
import com.webauthn4j.data.PublicKeyCredentialUserEntity;
import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import com.yubico.webauthn.data.UserIdentity;
import java.net.URL;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Disabled;
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

    @Test
    public void testWebAuthn4j() throws Exception {
        PublicKeyCredentialRpEntity rp = new PublicKeyCredentialRpEntity("localhost", "RP Name");
        byte[] userId = new byte[32];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(userId);
        PublicKeyCredentialUserEntity user = new PublicKeyCredentialUserEntity(userId, "username", "display name");
        Challenge challenge = new DefaultChallenge();
        List<PublicKeyCredentialParameters> pubKeyCredParams = new LinkedList<>();
        PublicKeyCredentialCreationOptions creationOptions = new PublicKeyCredentialCreationOptions(rp, user, challenge, pubKeyCredParams);
        JsonConverter jsonConverter = new ObjectConverter().getJsonConverter();
        String request = jsonConverter.writeValueAsString(creationOptions);
        LOGGER.debug("request: {}", request);
    }

    @Test
    @Disabled
    public void testAAGUID() throws Exception {
        URL aaguidUrl = new URL("https://raw.githubusercontent.com/passkeydeveloper/passkey-authenticator-aaguids/main/combined_aaguid.json");
        byte[] data = IOUtils.toByteArray(aaguidUrl);
        LOGGER.debug("data: {}", new String(data));
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode aaguidJsonNode = objectMapper.readTree(aaguidUrl);
        JsonNode entry = aaguidJsonNode.findValue("adce0002-35bc-c60a-648b-0b25f1f05503");
        String name = entry.findValue("name").asText();
        LOGGER.debug("name: {}", name);
    }
}
