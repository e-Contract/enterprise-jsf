/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.pages;

import java.io.Serializable;

public class Page implements Serializable {

    private final String id;

    private boolean visited;

    public Page(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public boolean isVisited() {
        return this.visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }
}
