/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.unit.be.e_contract.ejsf.validator;

import be.e_contract.ejsf.validator.EmailValidator;
import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EmailValidatorTest {

    private EmailValidator testedInstance;

    @BeforeEach
    public void setUp() {
        this.testedInstance = new EmailValidator();
    }

    @Test
    public void testNullOrEmptyPasses() throws Exception {
        this.testedInstance.validate(null, null, null);
        this.testedInstance.validate(null, null, "");
    }

    @Test
    public void testEmailPasses() throws Exception {
        this.testedInstance.validate(null, null, "info@e-contract.be");
    }

    @Test
    public void testMultipleEmailPasses() throws Exception {
        this.testedInstance.setAllowMultiple(true);
        this.testedInstance.validate(null, null, "info@e-contract.be,info@e-contract.be");
    }

    @Test
    public void testMultipleEmailFails() throws Exception {
        FacesContext mockFacesContext = EasyMock.mock(FacesContext.class);
        Application mockApplication = EasyMock.mock(Application.class);
        EasyMock.expect(mockFacesContext.getApplication()).andReturn(mockApplication);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("be.e_contract.ejsf.Messages");
        EasyMock.expect(mockApplication.getResourceBundle(mockFacesContext, "ejsfMessages")).andReturn(resourceBundle);

        EasyMock.replay(mockFacesContext, mockApplication);

        this.testedInstance.setAllowMultiple(true);
        Assertions.assertThrows(ValidatorException.class, ()
                -> this.testedInstance.validate(mockFacesContext, null, "info@e-contract.be,foobar"));

        EasyMock.verify(mockFacesContext, mockApplication);
    }

    @Test
    public void testNonEmailFails() throws Exception {
        FacesContext mockFacesContext = EasyMock.mock(FacesContext.class);
        Application mockApplication = EasyMock.mock(Application.class);
        EasyMock.expect(mockFacesContext.getApplication()).andReturn(mockApplication);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("be.e_contract.ejsf.Messages");
        EasyMock.expect(mockApplication.getResourceBundle(mockFacesContext, "ejsfMessages")).andReturn(resourceBundle);

        EasyMock.replay(mockFacesContext, mockApplication);

        Assertions.assertThrows(ValidatorException.class, ()
                -> this.testedInstance.validate(mockFacesContext, null, "foobar"));

        EasyMock.verify(mockFacesContext, mockApplication);
    }
}
