/*
 * Enterprise JSF project.
 *
 * Copyright 2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.input.template;

import javax.faces.view.facelets.MetaRule;
import javax.faces.view.facelets.Metadata;
import javax.faces.view.facelets.MetadataTarget;
import javax.faces.view.facelets.TagAttribute;

public class ResultMetaRule extends MetaRule {

    @Override
    public Metadata applyRule(String name, TagAttribute attribute, MetadataTarget meta) {
        if (!"result".equals(name)) {
            return null;
        }
        String value = attribute.getValue();
        return new ResultMetadata(value);
    }
}
