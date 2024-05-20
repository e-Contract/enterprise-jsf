/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import java.io.IOException;
import java.io.InputStream;
import jakarta.inject.Named;
import org.apache.commons.io.IOUtils;

@Named("mermaidController")
public class MermaidController {

    public String getDiagram() {
        InputStream inputStream
                = MermaidController.class.getResourceAsStream("/diagram.mermaid");
        try {
            return IOUtils.toString(inputStream, "UTF-8");
        } catch (IOException ex) {
            return "";
        }
    }
}
