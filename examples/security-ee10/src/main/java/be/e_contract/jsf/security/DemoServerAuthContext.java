package be.e_contract.jsf.security;

import java.util.Collections;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import jakarta.security.auth.message.AuthException;
import jakarta.security.auth.message.AuthStatus;
import jakarta.security.auth.message.MessageInfo;
import jakarta.security.auth.message.config.ServerAuthContext;
import jakarta.security.auth.message.module.ServerAuthModule;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoServerAuthContext implements ServerAuthContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoServerAuthContext.class);

    private final ServerAuthModule serverAuthModule;

    public DemoServerAuthContext(CallbackHandler handler) throws AuthException {
        this.serverAuthModule = new DemoServerAuthModule();
        this.serverAuthModule.initialize(null, null, handler, Collections.emptyMap());
    }

    @Override
    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject) throws AuthException {
        LOGGER.debug("validateRequest");
        HttpServletRequest httpServletRequest = (HttpServletRequest) messageInfo.getRequestMessage();
        String requestUri = httpServletRequest.getRequestURI();
        LOGGER.debug("request URI: {}", requestUri);
        return this.serverAuthModule.validateRequest(messageInfo, clientSubject,
                serviceSubject);
    }

    @Override
    public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {
        LOGGER.debug("secureResponse");
        return this.serverAuthModule.secureResponse(messageInfo, serviceSubject);
    }

    @Override
    public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {
        LOGGER.debug("cleanSubject");
        this.serverAuthModule.cleanSubject(messageInfo, subject);
    }
}
