package be.e_contract.jsf.security;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import jakarta.enterprise.util.Nonbinding;
import jakarta.interceptor.InterceptorBinding;

/**
 * Provides RBAC security using the servlet container security context.
 *
 * @author Frank Cornelis
 */
@Inherited
@InterceptorBinding
@Retention(RUNTIME)
@Target({METHOD, TYPE})
public @interface RoleAllowed {

    /**
     * The role that is allowed to invoke the method.
     *
     * @return
     */
    @Nonbinding
    String value();
}
