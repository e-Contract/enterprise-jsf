package be.e_contract.jsf.security;

import java.io.Serializable;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("loginController")
@ViewScoped
public class LoginController implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    private String username;

    private String password;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void login() {
        LOGGER.debug("login: {} - {}", this.username, this.password);
        DemoServerAuthModule.loginFromJSF(this.username, this.password);
    }
}
