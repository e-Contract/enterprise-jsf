package be.e_contract.jsf.helloworld;

import be.e_contract.jsf.taglib.validator.ExamplePartialStateHolderValidator;
import java.io.Serializable;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.PostAddToViewEvent;
import javax.faces.validator.Validator;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@ViewScoped
public class ValidatorController implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidatorController.class);

    private String value;

    private boolean changeAjax;

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isChangeAjax() {
        return this.changeAjax;
    }

    public void setChangeAjax(boolean changeAjax) {
        this.changeAjax = changeAjax;
    }

    public void postAddToView(PostAddToViewEvent event) {
        LOGGER.debug("postAddToView event");
        UIInput inputComponent = (UIInput) event.getComponent();
        changeComponentValidator(inputComponent, "We changed the message during postAddToView.");
    }

    public void ajaxEvent(AjaxBehaviorEvent event) {
        LOGGER.debug("AJAX event");
        if (!this.changeAjax) {
            return;
        }
        UIComponent component = event.getComponent();
        UIInput inputComponent = (UIInput) component.findComponent("input");
        changeComponentValidator(inputComponent, "We changed the message during AJAX listener.");
    }

    private void changeComponentValidator(UIInput inputComponent, String message) {
        for (Validator validator : inputComponent.getValidators()) {
            if (validator instanceof ExamplePartialStateHolderValidator) {
                ExamplePartialStateHolderValidator exampleValidator = (ExamplePartialStateHolderValidator) validator;
                exampleValidator.setMessage(message);
                return;
            }
        }
    }
}
