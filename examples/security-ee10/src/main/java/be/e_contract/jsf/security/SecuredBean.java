package be.e_contract.jsf.security;

import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateless;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RolesAllowed("my-role")
@Stateless
public class SecuredBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecuredBean.class);

    @Resource
    private SessionContext sessionContext;

    public void someMethod() {
        LOGGER.debug("invoking a secured method");
        boolean myRole = this.sessionContext.isCallerInRole("my-role");
        boolean foobarRole = this.sessionContext.isCallerInRole("foobar");
        LOGGER.debug("caller in my-role: {}", myRole);
        LOGGER.debug("caller in foobar role: {}", foobarRole);
        LOGGER.debug("caller principal: {}", this.sessionContext.getCallerPrincipal());
    }
}
