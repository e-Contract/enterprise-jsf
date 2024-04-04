package be.e_contract.jsf.security;

import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import jakarta.security.auth.message.AuthException;
import jakarta.security.auth.message.MessageInfo;
import jakarta.security.auth.message.config.ServerAuthConfig;
import jakarta.security.auth.message.config.ServerAuthContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoServerAuthConfig implements ServerAuthConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoServerAuthConfig.class);

    private final String layer;
    private final String appContext;
    private final CallbackHandler handler;

    public DemoServerAuthConfig(String layer, String appContext, CallbackHandler handler) {
        this.layer = layer;
        this.appContext = appContext;
        this.handler = handler;
    }

    @Override
    public ServerAuthContext getAuthContext(String authContextID, Subject serviceSubject, Map properties) throws AuthException {
        LOGGER.debug("getAuthContext: {}", authContextID);
        ServerAuthContext serverAuthContext = new DemoServerAuthContext(this.handler);
        return serverAuthContext;
    }

    @Override
    public String getMessageLayer() {
        LOGGER.debug("getMessageLayer");
        return this.layer;
    }

    @Override
    public String getAppContext() {
        LOGGER.debug("getAppContext");
        return this.appContext;
    }

    @Override
    public String getAuthContextID(MessageInfo messageInfo) {
        LOGGER.debug("getAuthContextID");
        return this.appContext;
    }

    @Override
    public void refresh() {
        LOGGER.debug("refresh");
    }

    @Override
    public boolean isProtected() {
        LOGGER.debug("isProtected");
        return false;
    }
}
