/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.test;

import javax.faces.component.UIComponent;

public class InfoComponentTest implements ComponentTest {

    private final String library;

    private final String tagName;

    private final String componentType;

    public InfoComponentTest(String library, String tagName, String componentType) {
        this.library = library;
        this.tagName = tagName;
        this.componentType = componentType;
    }

    @Override
    public String getName() {
        return "Info";
    }

    @Override
    public String run(TestComponentComponent testComponent, UIComponent component) {
        String rendererType = component.getRendererType();
        String family = component.getFamily();
        String componentClassName = component.getClass().getName();
        String result = "Library: " + this.library + "\n"
                + "Tag: " + this.tagName + "\n"
                + "Component type: " + this.componentType + "\n"
                + "Component class: " + componentClassName + "\n"
                + "Family: " + family + "\n"
                + "Renderer type: " + rendererType;
        return result;
    }

    @Override
    public void cleanup(TestComponentComponent testComponent, UIComponent component) {
    }
}
