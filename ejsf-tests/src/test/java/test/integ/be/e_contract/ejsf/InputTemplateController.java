/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.integ.be.e_contract.ejsf;

import jakarta.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;
import test.integ.be.e_contract.ejsf.cdi.TestScoped;

@Named
@TestScoped
public class InputTemplateController implements Serializable {

    private String template;

    private String result;

    public void loadTemplate(String templateResourceName) {
        try {
            this.template = IOUtils.resourceToString(templateResourceName, Charset.forName("UTF-8"));
        } catch (IOException ex) {
            return;
        }
    }

    public String getTemplate() {
        return this.template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
