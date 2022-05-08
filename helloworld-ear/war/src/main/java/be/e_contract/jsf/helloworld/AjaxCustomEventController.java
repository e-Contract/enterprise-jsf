package be.e_contract.jsf.helloworld;

import be.e_contract.jsf.taglib.ExampleAjaxBehaviorEvent;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

@Named
@ViewScoped
public class AjaxCustomEventController implements Serializable {

    public void ajaxListener(ExampleAjaxBehaviorEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO,
                "clicked: " + event.getParameter(), null);
        facesContext.addMessage(null, facesMessage);
    }
}
