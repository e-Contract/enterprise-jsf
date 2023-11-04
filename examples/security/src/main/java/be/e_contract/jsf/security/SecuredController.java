package be.e_contract.jsf.security;

import java.io.IOException;
import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RoleAllowed("my-role")
@Named("securedController")
public class SecuredController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecuredController.class);

    @Inject
    private UnauthorizedController unauthorizedController;

    @EJB
    private SecuredBean securedBean;

    @EJB
    private UnauthorizedBean unauthorizedBean;

    public String getValue() {
        try {
            this.unauthorizedController.someMethod();
            LOGGER.error("woopsy");
        } catch (SecurityException ex) {
            LOGGER.debug("CDI RBAC is working as expected");
        }
        this.securedBean.someMethod();
        try {
            this.unauthorizedBean.someMethod();
            LOGGER.error("woopsy");
        } catch (EJBAccessException ex) {
            LOGGER.debug("EJB RBAC is working as expected");
        }
        return "hello world";
    }

    public void logout() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletRequest httpServletRequest = (HttpServletRequest) externalContext.getRequest();
        try {
            httpServletRequest.logout();
        } catch (ServletException ex) {
            LOGGER.error("logout error: " + ex.getMessage(), ex);
        }
        try {
            externalContext.redirect("/security/index.xhtml");
        } catch (IOException ex) {
            LOGGER.error("redirect error: " + ex.getMessage(), ex);
        }
    }

    public void securedMethod() {
        this.securedBean.someMethod();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Secure method invoked.", null));
    }
}
