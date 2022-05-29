package be.e_contract.jsf.helloworld;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

@Named
@ViewScoped
public class PageController implements Serializable {

    private String value;

    @PostConstruct
    public void postConstruct() {
        this.value = "test";
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void action() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage globalFacesMessage
                = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Action performed", null);
        facesContext.addMessage(null, globalFacesMessage);

        FacesMessage inputFacesMessage
                = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Input is " + this.value, null);
        facesContext.addMessage("mainForm:input", inputFacesMessage);
    }
}
