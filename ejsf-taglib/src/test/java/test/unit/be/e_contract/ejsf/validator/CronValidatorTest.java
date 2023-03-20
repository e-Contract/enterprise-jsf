/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.unit.be.e_contract.ejsf.validator;

import be.e_contract.ejsf.validator.CronValidator;
import javax.faces.validator.ValidatorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CronValidatorTest {

    private CronValidator testedInstance;

    @BeforeEach
    public void setUp() {
        this.testedInstance = new CronValidator();
    }

    @Test
    public void testNullOrEmptyPasses() throws Exception {
        this.testedInstance.validate(null, null, null);
        this.testedInstance.validate(null, null, "");
    }

    @Test
    public void testCronPasses() throws Exception {
        this.testedInstance.setCronType("UNIX");
        this.testedInstance.validate(null, null, "0 0 * * *");
        this.testedInstance.setCronType("SPRING");
        this.testedInstance.validate(null, null, "0 0 0 * * *");
    }

    @Test
    public void testCronFails() throws Exception {
        this.testedInstance.setCronType("UNIX");
        Assertions.assertThrows(ValidatorException.class,
                () -> this.testedInstance.validate(null, null, "foobar")
        );
    }

}
