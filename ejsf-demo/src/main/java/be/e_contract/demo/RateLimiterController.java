/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named("rateLimiterController")
@RequestScoped
public class RateLimiterController {

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
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "username: " + this.username, null));
    }

    public void onLimit(String username) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Trying to hack into " + username + "?", null));
    }
}
