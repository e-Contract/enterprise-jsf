package be.e_contract.jsf.helloworld;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.event.PhaseId;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

@Named
@ViewScoped
public class LifecycleController implements Serializable {

    private String param;

    public String getParam() {
        return this.param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    @PostConstruct
    public void postConstruct() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage facesMessage
                = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "@PostConstruct: " + getInfo(), null);
        facesContext.addMessage(null, facesMessage);
    }

    public void viewAction() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage facesMessage
                = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "viewAction: " + getInfo(), null);
        facesContext.addMessage(null, facesMessage);
    }

    public void preRenderView() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage facesMessage
                = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "preRenderView: " + getInfo(), null);
        facesContext.addMessage(null, facesMessage);
    }

    public void postbackListener() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage facesMessage
                = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "postbackListener: " + getInfo(), null);
        facesContext.addMessage(null, facesMessage);
    }

    public void postback() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage facesMessage
                = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "postback: " + getInfo(), null);
        facesContext.addMessage(null, facesMessage);
    }

    public void ajaxListener() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage facesMessage
                = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "ajaxListener: " + getInfo(), null);
        facesContext.addMessage(null, facesMessage);
    }

    public void ajax() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage facesMessage
                = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "ajax: " + getInfo(), null);
        facesContext.addMessage(null, facesMessage);
    }

    private String getInfo() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        PhaseId phaseId = facesContext.getCurrentPhaseId();
        boolean postback = facesContext.isPostback();
        PartialViewContext partialViewContext
                = facesContext.getPartialViewContext();
        boolean ajaxRequest = partialViewContext.isAjaxRequest();
        return "param: " + this.param + ", isPostback: " + postback
                + ", isAjaxRequest: " + ajaxRequest
                + ", phaseId: " + phaseId.getName();
    }
}
