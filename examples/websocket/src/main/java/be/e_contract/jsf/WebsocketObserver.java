package be.e_contract.jsf;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.faces.event.WebsocketEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class WebsocketObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketObserver.class);

    public void onOpen(@Observes @WebsocketEvent.Opened WebsocketEvent event) {
        LOGGER.debug("onOpen");
    }

    public void onClose(@Observes @WebsocketEvent.Closed WebsocketEvent event) {
        LOGGER.debug("onClose");
    }
}
