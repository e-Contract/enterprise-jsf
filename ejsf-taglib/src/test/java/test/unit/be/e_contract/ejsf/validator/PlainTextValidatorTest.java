/*
 * Enterprise JSF project.
 *
 * Copyright 2025 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.unit.be.e_contract.ejsf.validator;

import be.e_contract.ejsf.validator.PlainTextValidator;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlainTextValidatorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlainTextValidatorTest.class);

    private PlainTextValidator testedInstance;

    @BeforeEach
    public void setUp() {
        this.testedInstance = new PlainTextValidator();
    }

    @Test
    public void testNullOrEmptyPasses() throws Exception {
        this.testedInstance.validate(null, null, null);
        this.testedInstance.validate(null, null, "");
    }

    @Test
    public void testPlainTextPasses() throws Exception {
        this.testedInstance.validate(null, null, "hello world");
    }

    @Test
    public void testHTMLFails() throws Exception {
        FacesContext mockFacesContext = EasyMock.mock(FacesContext.class);
        Application mockApplication = EasyMock.mock(Application.class);
        EasyMock.expect(mockFacesContext.getApplication()).andReturn(mockApplication);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("be.e_contract.ejsf.Messages", Locale.ENGLISH);
        EasyMock.expect(mockApplication.getResourceBundle(mockFacesContext, "ejsfMessages")).andReturn(resourceBundle);

        EasyMock.replay(mockFacesContext, mockApplication);

        ValidatorException exception = Assertions.assertThrows(ValidatorException.class,
                () -> this.testedInstance.validate(mockFacesContext, null, "<b>test</b>")
        );
        String summary = exception.getFacesMessage().getSummary();
        LOGGER.debug("JSF message: {}", summary);
        Assertions.assertEquals("Invalid characters.", summary);

        EasyMock.verify(mockFacesContext, mockApplication);
    }

    @Test
    public void testEmailAddressFails() throws Exception {
        FacesContext mockFacesContext = EasyMock.mock(FacesContext.class);
        Application mockApplication = EasyMock.mock(Application.class);
        EasyMock.expect(mockFacesContext.getApplication()).andReturn(mockApplication);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("be.e_contract.ejsf.Messages", Locale.ENGLISH);
        EasyMock.expect(mockApplication.getResourceBundle(mockFacesContext, "ejsfMessages")).andReturn(resourceBundle);

        EasyMock.replay(mockFacesContext, mockApplication);

        ValidatorException exception = Assertions.assertThrows(ValidatorException.class,
                () -> this.testedInstance.validate(mockFacesContext, null, "info@e-contract.be")
        );
        String summary = exception.getFacesMessage().getSummary();
        LOGGER.debug("JSF message: {}", summary);
        Assertions.assertEquals("Invalid characters.", summary);

        EasyMock.verify(mockFacesContext, mockApplication);
    }

    @Test
    public void testSpike() throws Exception {
        PolicyFactory policy = new HtmlPolicyBuilder().toFactory();
        String origValue = "info@e-contract.be";
        String result = policy.sanitize(origValue);
        LOGGER.debug("original string: {}", origValue);
        LOGGER.debug("result: {}", result);
    }

    @Test
    public void testAllowEmail() throws Exception {
        this.testedInstance.setAllowEmail(true);
        this.testedInstance.validate(null, null, "info_test@e-contract.be");
    }

    /**
     * Tests for CVE-2025-66021.
     *
     * @throws Exception
     */
    @Test
    public void testSanitizerVulnerability() throws Exception {
        HtmlPolicyBuilder htmlPolicyBuilder = new HtmlPolicyBuilder();
        PolicyFactory vulnerablePolicy = htmlPolicyBuilder
                .allowElements("style", "noscript")
                .allowTextIn("style")
                .toFactory();
        String payload = "<noscript><style></noscript><script>alert(1)</script>";

        LOGGER.debug("payload: {}", payload);
        LOGGER.debug("sanitized: {}", vulnerablePolicy.sanitize(payload));

        htmlPolicyBuilder = new HtmlPolicyBuilder();
        PolicyFactory ourPolicy = htmlPolicyBuilder.toFactory();
        LOGGER.debug("payload: {}", payload);
        LOGGER.debug("sanitized: {}", ourPolicy.sanitize(payload));
    }
}
