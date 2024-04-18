/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

@Named("lastReviewedController")
@ViewScoped
public class LastReviewedController implements Serializable {

    private List<LocalDateTime> values;

    @PostConstruct
    public void postConstruct() {
        this.values = new LinkedList<>();
        this.values.add(LocalDateTime.now());
        this.values.add(LocalDateTime.now().minusMonths(2));
        this.values.add(LocalDateTime.now().minusMonths(4));
        this.values.add(LocalDateTime.now().minusMonths(7));
        this.values.add(null);
    }

    public List<LocalDateTime> getValues() {
        return this.values;
    }
}
