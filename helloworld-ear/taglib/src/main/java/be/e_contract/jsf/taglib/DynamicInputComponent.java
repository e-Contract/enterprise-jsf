package be.e_contract.jsf.taglib;

import java.io.IOException;
import javax.faces.application.Application;
import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ComponentSystemEventListener;
import javax.faces.event.ListenerFor;
import javax.faces.event.PostAddToViewEvent;

@FacesComponent(DynamicInputComponent.COMPONENT_TYPE)
@ListenerFor(systemEventClass = PostAddToViewEvent.class)
public class DynamicInputComponent extends UIInput
        implements ComponentSystemEventListener, NamingContainer {

    public static final String COMPONENT_TYPE = "ejsf.dynamicInput";

    public static final String COMPONENT_FAMILY = "ejsf";

    public DynamicInputComponent() {
        setRendererType(null);
    }

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

            UIComponent textInputComponent
                    = application.createComponent(
                            HtmlInputText.COMPONENT_TYPE);
            textInputComponent.setId("text");
            getChildren().add(textInputComponent);

            UIComponent booleanInputComponent
                    = application.createComponent(
                            HtmlSelectBooleanCheckbox.COMPONENT_TYPE);
            booleanInputComponent.setId("boolean");
            getChildren().add(booleanInputComponent);
        }
        super.processEvent(event);
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    private UIInput findInputComponent() {
        Class type = getType();
        if (type.equals(String.class)) {
            return (UIInput) findComponent("text");
        }
        if (type.equals(Boolean.class)) {
            return (UIInput) findComponent("boolean");
        }
        return null;
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        ResponseWriter responseWriter = context.getResponseWriter();
        String clientId = super.getClientId(context);
        responseWriter.startElement("span", this);
        responseWriter.writeAttribute("id", clientId, "id");
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
    public void encodeEnd(FacesContext context) throws IOException {
        ResponseWriter responseWriter = context.getResponseWriter();
        responseWriter.endElement("span");
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
