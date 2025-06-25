/*
 * Enterprise JSF project.
 *
 * Copyright 2023-2025 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.unit.be.e_contract.ejsf.validator;

import be.e_contract.ejsf.validator.TrimValidator;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TrimValidatorTest {

    private TrimValidator testedInstance;

    @BeforeEach
    public void setUp() {
        this.testedInstance = new TrimValidator();
    }

    @Test
    public void testNullPasses() throws Exception {
        this.testedInstance.validate(null, null, null);
    }

    @Test
    public void testNormalPasses() throws Exception {
        this.testedInstance.validate(null, null, "https://service.provider/end-point");
    }

    @Test
    public void testLeadingTrailingWhiteSpaces() throws Exception {
        FacesContext mockFacesContext = EasyMock.mock(FacesContext.class);
        Application mockApplication = EasyMock.mock(Application.class);
        EasyMock.expect(mockFacesContext.getApplication()).andReturn(mockApplication);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("be.e_contract.ejsf.Messages", Locale.ENGLISH);
        EasyMock.expect(mockApplication.getResourceBundle(mockFacesContext, "ejsfMessages")).andReturn(resourceBundle);

        EasyMock.replay(mockFacesContext, mockApplication);

        ValidatorException exception = Assertions.assertThrows(ValidatorException.class,
                () -> this.testedInstance.validate(mockFacesContext, null, " https://service.provider/end-point")
        );
        Assertions.assertTrue(exception.getFacesMessage().getSummary().contains("white spaces"));

        EasyMock.verify(mockFacesContext, mockApplication);
    }
}
