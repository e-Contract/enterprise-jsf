package be.e_contract.jsf.security;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class DemoHttpSessionListener implements HttpSessionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoHttpSessionListener.class);

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        LOGGER.debug("session destroyed: {}", event.getSession().getId());
    }

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        LOGGER.debug("session created: {}", event.getSession().getId());
    }
}
