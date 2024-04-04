package be.e_contract.jsf.security;

import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import jakarta.security.auth.message.AuthException;
import jakarta.security.auth.message.config.AuthConfigFactory;
import jakarta.security.auth.message.config.AuthConfigProvider;
import jakarta.security.auth.message.config.ClientAuthConfig;
import jakarta.security.auth.message.config.ServerAuthConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoAuthConfigProvider implements AuthConfigProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoAuthConfigProvider.class);

    public DemoAuthConfigProvider() {
        LOGGER.debug("default constructor");
    }

    public DemoAuthConfigProvider(Map<String, String> properties, AuthConfigFactory factory) {
        LOGGER.debug("main constructor");
        if (null != factory) {
            factory.registerConfigProvider(this, null, null, "Auto registration");
        }
    }

    @Override
    public ClientAuthConfig getClientAuthConfig(String layer, String appContext, CallbackHandler handler) throws AuthException {
        LOGGER.debug("getClientAuthConfig");
        return null;
    }

    @Override
    public ServerAuthConfig getServerAuthConfig(String layer, String appContext, CallbackHandler handler) throws AuthException {
        LOGGER.debug("getServerAuthConfig: layer: {}, app context: {}", layer, appContext);
        ServerAuthConfig serverAuthConfig = new DemoServerAuthConfig(layer, appContext, handler);
        return serverAuthConfig;
    }

    @Override
    public void refresh() {
        LOGGER.debug("refresh");
    }
}
