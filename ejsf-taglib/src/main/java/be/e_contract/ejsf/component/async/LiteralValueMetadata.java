/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.async;

import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.Metadata;

public class LiteralValueMetadata extends Metadata {

    private final String value;

    public LiteralValueMetadata(String value) {
        this.value = value;
    }

    @Override
    public void applyMetadata(FaceletContext faceletContext, Object instance) {
        AsyncComponent asyncComponent = (AsyncComponent) instance;
        asyncComponent.setValue(this.value);
    }
}
