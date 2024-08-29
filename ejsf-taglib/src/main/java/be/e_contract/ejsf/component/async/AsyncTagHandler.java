/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.async;

import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.MetaRule;
import javax.faces.view.facelets.MetaRuleset;

public class AsyncTagHandler extends ComponentHandler {

    private static final MetaRule LITERAL_VALUE_META_RULE = new LiteralValueMetaRule();

    public AsyncTagHandler(ComponentConfig componentConfig) {
        super(componentConfig);
    }

    @Override
    protected MetaRuleset createMetaRuleset(Class type) {
        MetaRuleset metaRuleset = super.createMetaRuleset(type);
        metaRuleset.addRule(LITERAL_VALUE_META_RULE);
        return metaRuleset;
    }
}
