/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.integ.be.e_contract.ejsf;

import jakarta.inject.Named;
import test.integ.be.e_contract.ejsf.cdi.TestScoped;

@Named
@TestScoped
public class RobotsController {

    private boolean index;
    private boolean follow;

    public boolean isIndex() {
        return this.index;
    }

    public void setIndex(boolean index) {
        this.index = index;
    }

    public boolean isFollow() {
        return this.follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }
}
