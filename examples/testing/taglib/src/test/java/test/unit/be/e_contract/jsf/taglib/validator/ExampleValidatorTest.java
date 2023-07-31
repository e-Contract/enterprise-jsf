package test.unit.be.e_contract.jsf.taglib.validator;

import be.e_contract.jsf.taglib.validator.ExampleValidator;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExampleValidatorTest {

    private ExampleValidator testedInstance;

    @BeforeEach
    public void beforeEach() {
        this.testedInstance = new ExampleValidator();
    }

    @Test
    public void testValidator() {
        this.testedInstance.validate(null, null, "hello world");
        ValidatorException result = assertThrows(ValidatorException.class,
                () -> {
                    this.testedInstance.validate(null, null, "foobar");
                });
        assertEquals(FacesMessage.SEVERITY_ERROR,
                result.getFacesMessage().getSeverity());
        assertEquals("foobar not allowed",
                result.getFacesMessage().getSummary());
    }
}
