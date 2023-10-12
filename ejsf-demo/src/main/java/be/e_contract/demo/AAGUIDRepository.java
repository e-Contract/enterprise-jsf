/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AAGUIDRepository {

    private final JsonNode aaguidJsonNode;

    public AAGUIDRepository() throws IOException, URISyntaxException {
        URL aaguidUrl = new URI("https://raw.githubusercontent.com/passkeydeveloper/passkey-authenticator-aaguids/main/combined_aaguid.json").toURL();
        ObjectMapper objectMapper = new ObjectMapper();
        this.aaguidJsonNode = objectMapper.readTree(aaguidUrl);
    }

    public String findName(String aaguid) {
        JsonNode entry = this.aaguidJsonNode.findValue(aaguid);
        if (null == entry) {
            return null;
        }
        String name = entry.findValue("name").asText();
        return name;
    }

}
