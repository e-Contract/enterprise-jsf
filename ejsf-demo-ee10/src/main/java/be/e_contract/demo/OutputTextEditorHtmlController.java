/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import jakarta.inject.Named;

@Named("outputTextEditorHtmlController")
public class OutputTextEditorHtmlController {

    public String getValue() {
        return "<ul><li>test</li><li class=\"ql-indent-1\">second level</li></ul>";
    }
}
