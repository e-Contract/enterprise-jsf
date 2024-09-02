package be.e_contract.jsf.taglib;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import javax.faces.event.AjaxBehaviorEvent;

public class ExampleAjaxBehaviorEvent extends AjaxBehaviorEvent {

    public static final String NAME = "click";

    private final String parameter;

    public ExampleAjaxBehaviorEvent(UIComponent component, Behavior behavior, String parameter) {
        super(component, behavior);
        this.parameter = parameter;
    }

    public String getParameter() {
        return this.parameter;
    }
}
