/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.unit.be.e_contract.ejsf;

import be.e_contract.ejsf.component.webauthn.WebAuthnUtils;
import com.yubico.webauthn.data.ByteArray;
import java.security.SecureRandom;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebAuthnUtilsTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebAuthnUtilsTest.class);

    @Test
    public void testAuthenticationPRF() throws Exception {
        String request = IOUtils.toString(WebAuthnUtilsTest.class.getResourceAsStream("/public-key-credential-request-options.json"), "UTF-8");
        LOGGER.debug("request: {}", request);
        String result = WebAuthnUtils.addAuthenticationPRF(request, (ByteArray credentialId) -> {
            byte[] salt = new byte[32];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(salt);
            return new ByteArray(salt);
        });
        LOGGER.debug("result: {}", result);
    }

    @Test
    public void testRegistrationPRF() throws Exception {
        String request = IOUtils.toString(WebAuthnUtilsTest.class.getResourceAsStream("/public-key-credential-creation-options.json"), "UTF-8");
        LOGGER.debug("request: {}", request);
        String result = WebAuthnUtils.addRegistrationPRF(request);
        LOGGER.debug("result: {}", result);
    }
}
