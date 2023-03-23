/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.test;

import java.io.Serializable;
import javax.faces.component.UIComponent;

public interface ComponentTest extends Serializable {

    String getName();

    void run(TestComponentComponent testComponent, UIComponent component);

    void cleanup(TestComponentComponent testComponent, UIComponent component);
}
