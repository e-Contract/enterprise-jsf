package be.e_contract.jsf.security;

import java.io.Serializable;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Interceptor
@RoleAllowed("")
public class RoleAllowedInterceptor implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleAllowedInterceptor.class);

    @Inject
    private HttpServletRequest httpServletRequest;

    @AroundInvoke
    public Object verifyRoleAllowed(InvocationContext invocationContext) throws Exception {
        LOGGER.debug("verifyRoleAllowed");
        RoleAllowed roleAllowedAnnotation = invocationContext.getMethod().getAnnotation(RoleAllowed.class);
        if (null != roleAllowedAnnotation) {
            String role = roleAllowedAnnotation.value();
            verifyRole(role);
        } else {
            roleAllowedAnnotation = invocationContext.getTarget().getClass().getAnnotation(RoleAllowed.class);
            if (null != roleAllowedAnnotation) {
                String role = roleAllowedAnnotation.value();
                verifyRole(role);
            }
        }
        return invocationContext.proceed();
    }

    private void verifyRole(String role) {
        if ("".equals(role)) {
            LOGGER.warn("missing role");
            throw new SecurityException("missing role");
        }
        LOGGER.debug("isUserInRole: {}", role);
        if (!this.httpServletRequest.isUserInRole(role)) {
            LOGGER.warn("user not in role: {}", role);
            throw new SecurityException("user not in role " + role);
        }
    }
}
