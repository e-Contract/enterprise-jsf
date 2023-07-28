package be.e_contract.jsf.helloworld;

import be.e_contract.jsf.taglib.ExampleAjaxBehaviorEvent;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named
public class AjaxCustomEventController {

    public void ajaxListener(ExampleAjaxBehaviorEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO,
                "clicked: " + event.getParameter(), null);
        facesContext.addMessage(null, facesMessage);
    }
}
