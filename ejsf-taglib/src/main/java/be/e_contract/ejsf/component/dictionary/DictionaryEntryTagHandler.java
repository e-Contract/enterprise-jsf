/*
 * Enterprise JSF project.
 *
 * Copyright 2022-2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.dictionary;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagException;
import javax.faces.view.facelets.TagHandler;

public class DictionaryEntryTagHandler extends TagHandler {

    public DictionaryEntryTagHandler(TagConfig tagConfig) {
        super(tagConfig);
    }

    @Override
    public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException {
        if (!ComponentHandler.isNew(parent)) {
            return;
        }
        if (!(parent instanceof DictionaryComponent)) {
            throw new TagException(this.tag, "DictionaryEntry can only be attached to Dictionary components.");
        }
        String key = getRequiredAttribute("key").getValue();
        String value = getRequiredAttribute("value").getValue(faceletContext);
        DictionaryComponent dictionaryComponent = (DictionaryComponent) parent;
        Map<String, String> messages = dictionaryComponent.getMessages();
        if (null == messages) {
            messages = new HashMap<>();
            dictionaryComponent.setMessages(messages);
        }
        messages.put(key, value);
    }
}
