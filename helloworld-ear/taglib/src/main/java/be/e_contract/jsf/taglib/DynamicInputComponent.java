package be.e_contract.jsf.taglib;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.Application;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ComponentSystemEventListener;
import javax.faces.event.ListenerFor;
import javax.faces.event.PostAddToViewEvent;

@FacesComponent(DynamicInputComponent.COMPONENT_TYPE)
@ListenerFor(systemEventClass = PostAddToViewEvent.class)
public class DynamicInputComponent extends UIInput implements ComponentSystemEventListener {

    private static final Logger LOGGER = Logger.getLogger(DynamicInputComponent.class.getName());

    public static final String COMPONENT_TYPE = "ejsf.dynamicInput";

    public static final String COMPONENT_FAMILY = "ejsf";

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        type,
    }

    public Class getType() {
        return (Class) getStateHelper().eval(PropertyKeys.type, null);
    }

    public void setType(Class type) {
        getStateHelper().put(PropertyKeys.type, type);
    }

    @Override
    public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
        if (event instanceof PostAddToViewEvent) {
            FacesContext facesContext = event.getFacesContext();
            Application application = facesContext.getApplication();

            HtmlInputText htmlInputText
                    = (HtmlInputText) application.createComponent(
                            HtmlInputText.COMPONENT_TYPE);
            htmlInputText.setId(getId() + "_text");
            getChildren().add(htmlInputText);

            HtmlSelectBooleanCheckbox htmlSelectBooleanCheckbox
                    = (HtmlSelectBooleanCheckbox) application.createComponent(
                            HtmlSelectBooleanCheckbox.COMPONENT_TYPE);
            htmlSelectBooleanCheckbox.setId(getId() + "_boolean");
            getChildren().add(htmlSelectBooleanCheckbox);
            LOGGER.log(Level.FINE, "number of children: {0}", getChildCount());
        }
        super.processEvent(event);
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    private UIInput findInputComponent() {
        List<UIComponent> children = getChildren();
        Class type = getType();
        UIInput inputComponent = null;
        if (type.equals(String.class)) {
            inputComponent = (UIInput) children.get(0);
        } else if (type.equals(Boolean.class)) {
            inputComponent = (UIInput) children.get(1);
        }
        return inputComponent;
    }

    @Override
    public void encodeChildren(FacesContext context) throws IOException {
        UIInput inputComponent = findInputComponent();
        if (null == inputComponent) {
            return;
        }
        inputComponent.setValue(getValue());
        inputComponent.encodeAll(context);
    }

    @Override
    public void decode(FacesContext context) {
        UIInput inputComponent = findInputComponent();
        if (null == inputComponent) {
            return;
        }
        inputComponent.decode(context);
        setSubmittedValue(inputComponent.getSubmittedValue());
    }
}
