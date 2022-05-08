package be.e_contract.jsf.helloworld;

import java.io.Serializable;
import java.time.LocalTime;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

@Named
@ViewScoped
public class AjaxController implements Serializable {

    public void ajaxListener() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO,
                "clicked: " + LocalTime.now(), null);
        facesContext.addMessage(null, facesMessage);
    }
}
