/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.test;

import javax.faces.component.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XSSComponentTest implements ComponentTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(XSSComponentTest.class);

    private final String attributeName;

    private String previousValue;

    public XSSComponentTest(String attributeName) {
        this.attributeName = attributeName;
    }

    @Override
    public String getName() {
        return "XSS test on attribute " + this.attributeName;
    }

    @Override
    public String run(TestComponentComponent testComponent, UIComponent component) {
        this.previousValue = (String) component.getAttributes().get(this.attributeName);
        String script = "<script>PF('" + testComponent.resolveWidgetVar() + "').failedTest('XSS on "
                + this.attributeName + " attribute');</script>";
        component.getAttributes().put(this.attributeName, script);
        LOGGER.debug("[{}] = {}", this.attributeName, script);
        return null;
    }

    @Override
    public void cleanup(TestComponentComponent testComponent, UIComponent component) {
        component.getAttributes().put(this.attributeName, this.previousValue);
    }
}
