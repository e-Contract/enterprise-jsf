package be.e_contract.jsf.helloworld;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named
public class ExampleFaceletsController {

    public void action() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage facesMessage
                = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Action performed", null);
        facesContext.addMessage(null, facesMessage);
    }
}
