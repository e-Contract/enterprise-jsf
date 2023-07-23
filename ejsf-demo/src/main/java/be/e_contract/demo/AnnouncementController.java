/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import java.io.Serializable;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

@Named("announcementController")
@ViewScoped
public class AnnouncementController implements Serializable {

    private String message;

    private int version;

    @PostConstruct
    public void postConstruct() {
        this.message = "This is an announcement.";
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        if (!Objects.equals(this.message, message)) {
            this.version++;
        }
        this.message = message;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
