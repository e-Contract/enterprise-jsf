package be.e_contract.jsf.security;

import java.util.LinkedList;
import java.util.List;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
@EJB(name = SecurityBean.JNDI_NAME, beanInterface = SecurityBean.class)
public class SecurityBean {

    public static final String JNDI_NAME = "java:app/SecurityBean";

    public List<String> authenticate(String username, String password) {
        if ("secret".equals(password)) {
            List<String> roles = new LinkedList<>();
            roles.add("my-role");
            return roles;
        }
        return null;
    }
}
