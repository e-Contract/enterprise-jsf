package be.e_contract.jsf.helloworld;

import java.time.LocalTime;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named
public class AjaxController {

    public void ajaxListener() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO,
                "clicked: " + LocalTime.now(), null);
        facesContext.addMessage(null, facesMessage);
    }
}
