/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import java.util.Date;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named("ageController")
@RequestScoped
public class AgeController {

    private Date dateOfBirth;

    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
