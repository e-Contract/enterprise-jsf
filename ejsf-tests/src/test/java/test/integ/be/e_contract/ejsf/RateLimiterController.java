/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.integ.be.e_contract.ejsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.integ.be.e_contract.ejsf.cdi.TestScoped;

@Named
@TestScoped
public class RateLimiterController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimiterController.class);

    private String username;

    private int actionCount;

    @PostConstruct
    public void postConstruct() {
        this.username = null;
        this.actionCount = 0;
    }

    public void onLimit(String username) {
        LOGGER.debug("onLimit: {}", username);
        this.username = username;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Reached limit.", null));
        PrimeFaces.current().ajax().update(":messages");
    }

    public String getUsername() {
        return this.username;
    }

    public void action() {
        this.actionCount++;
        LOGGER.debug("action: {}", this.actionCount);
    }

    public int getActionCount() {
        return this.actionCount;
    }
}
