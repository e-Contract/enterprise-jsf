/*
 * Enterprise JSF project.
 *
 * Copyright 2024-2025 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.commons.io.IOUtils;

@Named("outputLLMController")
@ViewScoped
public class OutputLLMController implements Serializable {

    private String value;

    @PostConstruct
    public void postConstruct() {
        InputStream inputStream
                = OutputLLMController.class.getResourceAsStream("/output-llm.md");
        try {
            this.value = IOUtils.toString(inputStream, "UTF-8");
        } catch (IOException ex) {
            this.value = "";
        }
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
