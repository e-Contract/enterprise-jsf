package be.e_contract.jsf;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("websocketController")
@ViewScoped
public class WebsocketController implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketController.class);

    @Inject
    @Push(channel = "demoChannel")
    private PushContext pushContext;

    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void sendMessage() {
        LOGGER.debug("send message: {}", this.message);
        this.pushContext.send(this.message);
        this.message = null;
    }

    public void listener(AjaxBehaviorEvent event) {
        FacesContext facesContext = event.getFacesContext();
        FacesMessage facesMessage = new FacesMessage("Message received");
        facesContext.addMessage(null, facesMessage);
    }
}
