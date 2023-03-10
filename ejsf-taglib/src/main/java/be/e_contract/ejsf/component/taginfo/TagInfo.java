/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.taginfo;

import java.util.LinkedList;
import java.util.List;

public class TagInfo {

    private final String tagName;
    private final String description;

    private final List<TagAttribute> requiredAttributes;
    private final List<TagAttribute> optionalAttributes;

    private final List<String> clientBehaviorEventNames;
    private String clientBehaviorDefaultEventName;

    public TagInfo(String tagName, String description) {
        this.tagName = tagName;
        this.description = description;
        this.requiredAttributes = new LinkedList<>();
        this.optionalAttributes = new LinkedList<>();
        this.clientBehaviorEventNames = new LinkedList<>();
    }

    public String getTagName() {
        return this.tagName;
    }

    public String getDescription() {
        return this.description;
    }

    public List<TagAttribute> getRequiredAttributes() {
        return this.requiredAttributes;
    }

    public List<TagAttribute> getOptionalAttributes() {
        return this.optionalAttributes;
    }

    public List<String> getClientBehaviorEventNames() {
        return this.clientBehaviorEventNames;
    }

    public String getClientBehaviorDefaultEventName() {
        return this.clientBehaviorDefaultEventName;
    }

    public void setClientBehaviorDefaultEventName(String clientBehaviorDefaultEventName) {
        this.clientBehaviorDefaultEventName = clientBehaviorDefaultEventName;
    }
}
