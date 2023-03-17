/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.unit.be.e_contract.ejsf.validator;

import be.e_contract.ejsf.validator.PINValidator;
import javax.faces.validator.ValidatorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PINValidatorTest {

    private PINValidator testedInstance;

    @BeforeEach
    public void setUp() {
        this.testedInstance = new PINValidator();
    }

    @Test
    public void testNullOrEmptyPasses() throws Exception {
        this.testedInstance.validate(null, null, null);
        this.testedInstance.validate(null, null, "");
    }

    @Test
    public void testPINPasses() throws Exception {
        this.testedInstance.validate(null, null, "1234");
    }

    @Test
    public void testPINTooShort() throws Exception {
        Assertions.assertThrows(ValidatorException.class,
                () -> this.testedInstance.validate(null, null, "123")
        );
    }

    @Test
    public void testFoobarFails() throws Exception {
        Assertions.assertThrows(ValidatorException.class,
                () -> this.testedInstance.validate(null, null, "foobar")
        );
    }
}
