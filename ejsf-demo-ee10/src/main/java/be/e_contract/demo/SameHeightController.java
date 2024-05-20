/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named("sameHeightController")
@ViewScoped
public class SameHeightController implements Serializable {

    private List<String> lines;

    @PostConstruct
    public void postConstruct() {
        this.lines = new LinkedList<>();
        this.lines.add("Line 0");
    }

    public List<String> getLines() {
        return this.lines;
    }

    public void addLine() {
        this.lines.add("Line " + this.lines.size());
    }
}
