/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.unit.be.e_contract.ejsf.validator;

import be.e_contract.ejsf.validator.OIDValidator;
import javax.faces.validator.ValidatorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OIDValidatorTest {

    private OIDValidator testedInstance;

    @BeforeEach
    public void setUp() {
        this.testedInstance = new OIDValidator();
    }

    @Test
    public void testNullOrEmptyPasses() throws Exception {
        this.testedInstance.validate(null, null, null);
        this.testedInstance.validate(null, null, "");
    }

    @Test
    public void testOIDPasses() throws Exception {
        this.testedInstance.validate(null, null, "1.2.3.4");
    }

    @Test
    public void testOIDFails() throws Exception {
        Assertions.assertThrows(ValidatorException.class,
                () -> this.testedInstance.validate(null, null, "123")
        );
        Assertions.assertThrows(ValidatorException.class,
                () -> this.testedInstance.validate(null, null, "10.0")
        );
        Assertions.assertThrows(ValidatorException.class,
                () -> this.testedInstance.validate(null, null, "1.01")
        );
    }

}
