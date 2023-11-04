package be.e_contract.jsf.security;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@RolesAllowed("foobar")
public class UnauthorizedBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnauthorizedBean.class);

    public void someMethod() {
        LOGGER.debug("could invoke an unauthorized method");
    }
}
