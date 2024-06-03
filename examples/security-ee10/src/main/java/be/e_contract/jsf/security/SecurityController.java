package be.e_contract.jsf.security;

public class SecurityController {

    public boolean authenticate(String username, String password) {
        return "secret".equals(password);
    }
}
