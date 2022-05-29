package be.e_contract.jsf.helloworld;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

@Named
@ViewScoped
public class ViewController implements Serializable {

    private String param;

    public String getParam() {
        return this.param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public void viewAction() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage globalFacesMessage
                = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "View parameter is: " + this.param, null);
        facesContext.addMessage(null, globalFacesMessage);
    }
}
