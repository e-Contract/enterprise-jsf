package be.e_contract.jsf.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import javax.security.auth.message.callback.GroupPrincipalCallback;
import javax.security.auth.message.module.ServerAuthModule;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoServerAuthModule implements ServerAuthModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoServerAuthModule.class);

    private static final String USERNAME_SESSION_ATTRIBUTE = DemoServerAuthModule.class.getName() + ".username";
    private static final String USERNAME_REQUEST_ATTRIBUTE = DemoServerAuthModule.class.getName() + ".username";
    private static final String PASSWORD_REQUEST_ATTRIBUTE = DemoServerAuthModule.class.getName() + ".password";
    private static final String TARGET_URI_SESSION_ATTRIBUTE = DemoServerAuthModule.class.getName() + ".targetUri";
    private static final String ROLES_SESSION_ATTRIBUTE = DemoServerAuthModule.class.getName() + ".roles";

    private static final String IS_MANDATORY = "javax.security.auth.message.MessagePolicy.isMandatory";
    private static final String REGISTER_SESSION = "javax.servlet.http.registerSession";

    private CallbackHandler handler;

    @Override
    public void initialize(MessagePolicy requestPolicy, MessagePolicy responsePolicy, CallbackHandler handler, Map options) throws AuthException {
        LOGGER.debug("initialize");
        this.handler = handler;
    }

    @Override
    public Class[] getSupportedMessageTypes() {
        LOGGER.debug("getSupportedMessageTypes");
        return new Class[]{HttpServletRequest.class, HttpServletResponse.class};
    }

    private void addPrincipalsToSubject(HttpSession httpSession, Subject clientSubject) {
        String username = (String) httpSession.getAttribute(USERNAME_SESSION_ATTRIBUTE);
        String[] roles = (String[]) httpSession.getAttribute(ROLES_SESSION_ATTRIBUTE);
        CallerPrincipalCallback callerPrincipalCallback
                = new CallerPrincipalCallback(clientSubject, username);
        GroupPrincipalCallback groupPrincipalCallback
                = new GroupPrincipalCallback(clientSubject, roles);
        Callback[] callbacks = new Callback[]{
            callerPrincipalCallback,
            groupPrincipalCallback
        };
        try {
            this.handler.handle(callbacks);
        } catch (IOException | UnsupportedCallbackException e) {
            LOGGER.error("error: " + e.getMessage(), e);
        }
    }

    @Override
    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject) throws AuthException {
        LOGGER.debug("validateRequest");
        HttpServletRequest httpServletRequest = (HttpServletRequest) messageInfo.getRequestMessage();
        HttpSession httpSession = httpServletRequest.getSession();
        LOGGER.debug("HTTP session: {}", httpSession.getId());
        Principal userPrincipal = httpServletRequest.getUserPrincipal();
        if (userPrincipal != null) {
            LOGGER.debug("has user principal");
            try {
                this.handler.handle(new Callback[]{
                    new CallerPrincipalCallback(clientSubject, userPrincipal)
                });
            } catch (IOException | UnsupportedCallbackException e) {
                LOGGER.error("error: " + e.getMessage(), e);
            }
            return AuthStatus.SUCCESS;
        }
        boolean mandatory = Boolean.parseBoolean((String) messageInfo.getMap().get(IS_MANDATORY));
        LOGGER.debug("mandatory: {}", mandatory);
        if (!mandatory) {
            return AuthStatus.SUCCESS;
        }
        HttpServletResponse httpServletResponse = (HttpServletResponse) messageInfo.getResponseMessage();
        String requestUri = httpServletRequest.getRequestURI();
        LOGGER.debug("request URI: {}", requestUri);
        String username = (String) httpSession.getAttribute(USERNAME_SESSION_ATTRIBUTE);
        if (null != username) {
            LOGGER.debug("user already authenticated");
            addPrincipalsToSubject(httpSession, clientSubject);
            messageInfo.getMap().put(REGISTER_SESSION, Boolean.TRUE.toString());
            return AuthStatus.SUCCESS;
        }
        username = (String) httpServletRequest.getAttribute(USERNAME_REQUEST_ATTRIBUTE);
        if (null == username) {
            saveTargetUri(httpServletRequest);
            gotoLogin(httpServletRequest, httpServletResponse);
            return AuthStatus.SEND_CONTINUE;
        }
        String password = (String) httpServletRequest.getAttribute(PASSWORD_REQUEST_ATTRIBUTE);
        SecurityBean securityBean;
        try {
            InitialContext initialContext = new InitialContext();
            securityBean = (SecurityBean) initialContext.lookup(SecurityBean.JNDI_NAME);
        } catch (NamingException ex) {
            throw new AuthException("JNDI error");
        }
        List<String> roles = securityBean.authenticate(username, password);
        if (null == roles) {
            LOGGER.warn("incorrect password: {}", password);
            return AuthStatus.SEND_FAILURE;
        }
        LOGGER.debug("successful login for user: {}", username);
        httpSession.setAttribute(USERNAME_SESSION_ATTRIBUTE, username);
        httpSession.setAttribute(ROLES_SESSION_ATTRIBUTE, roles.toArray(new String[0]));
        addPrincipalsToSubject(httpSession, clientSubject);
        gotoTargetUri(httpServletRequest, httpServletResponse);
        return AuthStatus.SUCCESS;

    }

    @Override
    public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {
        LOGGER.debug("secureResponse");
        return AuthStatus.SEND_SUCCESS;
    }

    @Override
    public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {
        LOGGER.debug("cleanSubject");
        HttpServletRequest httpServletRequest = (HttpServletRequest) messageInfo.getRequestMessage();
        HttpSession httpSession = httpServletRequest.getSession();
        httpSession.removeAttribute(TARGET_URI_SESSION_ATTRIBUTE);
        httpSession.removeAttribute(USERNAME_SESSION_ATTRIBUTE);
        httpSession.removeAttribute(ROLES_SESSION_ATTRIBUTE);
    }

    private void saveTargetUri(HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession();
        if (httpSession.getAttribute(TARGET_URI_SESSION_ATTRIBUTE) != null) {
            return;
        }
        String requestUri = httpServletRequest.getRequestURI();
        String queryString = httpServletRequest.getQueryString();
        String targetUri;
        if (null != queryString) {
            targetUri = requestUri + "?" + queryString;
        } else {
            targetUri = requestUri;
        }
        httpSession.setAttribute(TARGET_URI_SESSION_ATTRIBUTE, targetUri);
    }

    private void gotoLogin(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthException {
        String contextPath = httpServletRequest.getContextPath();
        String redirectLocation = contextPath + "/login.xhtml";
        LOGGER.debug("gotoLogin: {}", redirectLocation);
        sendRedirect(httpServletRequest, httpServletResponse, redirectLocation);
    }

    private void gotoTargetUri(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        HttpSession httpSession = httpServletRequest.getSession();
        String targetRequestUri = (String) httpSession.getAttribute(TARGET_URI_SESSION_ATTRIBUTE);
        if (null == targetRequestUri) {
            LOGGER.error("no target URI");
            return;
        }
        LOGGER.debug("redirecting to target URI: {}", targetRequestUri);
        sendRedirect(httpServletRequest, httpServletResponse, targetRequestUri);
    }

    public static void loginFromJSF(String username, String password) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletRequest httpServletRequest = (HttpServletRequest) externalContext.getRequest();
        if (httpServletRequest.getUserPrincipal() != null) {
            LOGGER.warn("already authenticated: {}", httpServletRequest.getUserPrincipal());
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Already authenticated.", null));
            return;
        }
        HttpServletResponse httpServletResponse = (HttpServletResponse) externalContext.getResponse();
        httpServletRequest.setAttribute(USERNAME_REQUEST_ATTRIBUTE, username);
        httpServletRequest.setAttribute(PASSWORD_REQUEST_ATTRIBUTE, password);
        try {
            boolean authenticated = httpServletRequest.authenticate(httpServletResponse);
            LOGGER.debug("authenticate result: {}", authenticated);
            if (authenticated) {
                facesContext.responseComplete();
            } else {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login error.", null));
            }
        } catch (IOException | ServletException ex) {
            // JBoss
            LOGGER.error("authentication error: " + ex.getMessage(), ex);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login error.", null));
        }
    }

    private boolean isFacesAjaxRequest(HttpServletRequest httpServletRequest) {
        String facesRequest = httpServletRequest.getHeader("Faces-Request");
        if (null == facesRequest) {
            return false;
        }
        LOGGER.debug("Faces-Request: {}", facesRequest);
        if ("partial/ajax".equals(facesRequest)) {
            return true;
        }
        if ("partial/process".equals(facesRequest)) {
            return true;
        }
        return false;
    }

    private void sendRedirect(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String location) {
        if (isFacesAjaxRequest(httpServletRequest)) {
            LOGGER.debug("sending JSF redirect: {}", location);
            httpServletResponse.setHeader("Cache-Control", "no-cache,no-store,must-revalidate");
            httpServletResponse.setDateHeader("Expires", 0);
            httpServletResponse.setHeader("Pragma", "no-cache");
            httpServletResponse.setContentType("text/xml");
            httpServletResponse.setCharacterEncoding("UTF-8");
            PrintWriter printWriter;
            try {
                printWriter = httpServletResponse.getWriter();
            } catch (IOException ex) {
                LOGGER.error("redirect error: " + ex.getMessage(), ex);
                return;
            }
            printWriter.printf("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    + "<partial-response><redirect url=\"%s\"/></partial-response>", location);
            try {
                httpServletResponse.flushBuffer();
            } catch (IOException ex) {
                LOGGER.error("redirect error: " + ex.getMessage(), ex);
            }
        } else {
            try {
                httpServletResponse.sendRedirect(location);
            } catch (IOException ex) {
                LOGGER.error("redirect error: " + ex.getMessage(), ex);
            }
        }
    }
}
