/*
 * Enterprise JSF project.
 *
 * Copyright 2023-2025 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.webauthn;

import java.io.IOException;
import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagException;
import javax.faces.view.facelets.TagHandler;

public class WebAuthnAuthenticateTagHandler extends TagHandler {

    private final TagAttribute forAttribute;

    public WebAuthnAuthenticateTagHandler(TagConfig tagConfig) {
        super(tagConfig);
        this.forAttribute = getAttribute("for");
    }

    @Override
    public void apply(FaceletContext context, UIComponent parent) throws IOException {
        if (!ComponentHandler.isNew(parent)) {
            return;
        }
        if (parent instanceof ActionSource) {
            ActionSource parentActionSource = (ActionSource) parent;
            String forValue;
            if (null != this.forAttribute) {
                forValue = this.forAttribute.getValue(context);
            } else {
                forValue = null;
            }
            WebAuthnAuthenticateActionListener actionListener = new WebAuthnAuthenticateActionListener(forValue);
            parentActionSource.addActionListener(actionListener);
            return;
        }
        throw new TagException(this.tag, "parent must be ActionSource");
    }
}
