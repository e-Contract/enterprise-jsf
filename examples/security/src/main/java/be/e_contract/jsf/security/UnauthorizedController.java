package be.e_contract.jsf.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RoleAllowed("foobar-role")
public class UnauthorizedController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnauthorizedController.class);

    public void someMethod() {
        LOGGER.debug("could invoke this unauthorized method");
    }
}
