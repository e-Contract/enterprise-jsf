package be.e_contract.jsf.taglib;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("ejsf.exampleValidator")
public class ExampleValidator implements Validator {

    @Override
    public void validate(FacesContext facesContext, UIComponent component, Object value) throws ValidatorException {
        if (null == value) {
            return;
        }
        if ("foobar".equals(value)) {
            FacesMessage facesMessage
                    = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "foobar not allowed", null);
            throw new ValidatorException(facesMessage);
        }
    }
}
