/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.output.enums;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagException;
import javax.faces.view.facelets.TagHandler;

public class EnumTagHandler extends TagHandler {

    public EnumTagHandler(TagConfig tagConfig) {
        super(tagConfig);
    }

    @Override
    public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException {
        if (!ComponentHandler.isNew(parent)) {
            return;
        }
        if (!(parent instanceof OutputEnumComponent)) {
            throw new TagException(this.tag, "Enum can only be attached to OutputEnum components.");
        }
        String name = getRequiredAttribute("name").getValue();
        String label;
        TagAttribute labelAttribute = getAttribute("label");
        if (null != labelAttribute) {
            label = labelAttribute.getValue(faceletContext);
        } else {
            label = null;
        }
        OutputEnumComponent outputEnumComponent = (OutputEnumComponent) parent;
        Map<String, OutputEnumComponent.EnumInfo> enums = outputEnumComponent.getEnums();
        if (null == enums) {
            enums = new HashMap<>();
            outputEnumComponent.setEnums(enums);
        }
        enums.put(name, new OutputEnumComponent.EnumInfo(name, label));
    }
}
