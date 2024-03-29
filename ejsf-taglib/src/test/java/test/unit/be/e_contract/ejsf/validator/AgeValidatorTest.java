/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.unit.be.e_contract.ejsf.validator;

import be.e_contract.ejsf.validator.AgeValidator;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AgeValidatorTest {

    private AgeValidator testedInstance;

    @BeforeEach
    public void setUp() {
        this.testedInstance = new AgeValidator();
    }

    @Test
    public void testNullPasses() throws Exception {
        this.testedInstance.validate(null, null, null);
    }

    @Test
    public void testAgeValidationTooYoung() throws Exception {
        this.testedInstance.setMinimumAge(18);

        FacesContext mockFacesContext = EasyMock.mock(FacesContext.class);
        Application mockApplication = EasyMock.mock(Application.class);
        EasyMock.expect(mockFacesContext.getApplication()).andReturn(mockApplication);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("be.e_contract.ejsf.Messages", Locale.ENGLISH);
        EasyMock.expect(mockApplication.getResourceBundle(mockFacesContext, "ejsfMessages")).andReturn(resourceBundle);

        EasyMock.replay(mockFacesContext, mockApplication);

        ValidatorException exception = Assertions.assertThrows(ValidatorException.class,
                () -> this.testedInstance.validate(mockFacesContext, null, new Date())
        );
        Assertions.assertTrue(exception.getFacesMessage().getSummary().contains("18"));

        EasyMock.verify(mockFacesContext, mockApplication);
    }
}
