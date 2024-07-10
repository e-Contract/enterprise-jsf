package be.e_contract.jsf;

import java.io.IOException;
import javax.enterprise.inject.spi.CDI;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.PongMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerEndpoint("/websocket/{group}")
public class DemoEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoEndpoint.class);

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        LOGGER.debug("onOpen");
        DirectController directController = getDirectController();
        if (directController.isBlock()) {
            CloseReason closeReason = new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "blocked");
            try {
                session.close(closeReason);
            } catch (IOException ex) {
                LOGGER.error("I/O error: " + ex.getMessage(), ex);
            }
            return;
        }
        session.setMaxIdleTimeout(1000 * 30);
        long maxIdleTimeout = session.getMaxIdleTimeout();
        LOGGER.debug("max idle timeout: {} ms", maxIdleTimeout);
        directController.addSession(session);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        LOGGER.debug("onClose");
        DirectController directController = getDirectController();
        directController.removeSession(session);
    }

    @OnMessage
    public void onPongMessage(Session session, PongMessage pongMessage) {
        LOGGER.debug("pong message");
    }

    @OnError
    public void onError(Session session, Throwable t) {
        LOGGER.debug("onError: {}", t.getMessage());
        DirectController directController = getDirectController();
        directController.removeSession(session);
    }

    private DirectController getDirectController() {
        DirectController directController = (DirectController) CDI.current().select(DirectController.class).get();
        return directController;
    }
}
