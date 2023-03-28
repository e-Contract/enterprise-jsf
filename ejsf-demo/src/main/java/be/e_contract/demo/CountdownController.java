/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

@Named("countdownCountroller")
@ViewScoped
public class CountdownController implements Serializable {

    @PostConstruct
    public void postConstruct() {
        this.countdowns = 10;
    }

    private int countdowns;

    public int getCountdowns() {
        return this.countdowns;
    }

    public void setCountdowns(int countdowns) {
        this.countdowns = countdowns;
    }

    public Integer[] getCountdownIndexes() {
        Integer[] countdownIndexes = new Integer[this.countdowns];
        for (int idx = 0; idx < this.countdowns; idx++) {
            countdownIndexes[idx] = idx;
        }
        return countdownIndexes;
    }
}
