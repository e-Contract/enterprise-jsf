/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.viewlogs;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagException;
import javax.faces.view.facelets.TagHandler;

public class ViewLogsExcludeTagHandler extends TagHandler {

    public ViewLogsExcludeTagHandler(TagConfig tagConfig) {
        super(tagConfig);
    }

    @Override
    public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException {
        if (!ComponentHandler.isNew(parent)) {
            return;
        }
        if (!(parent instanceof ViewLogsComponent)) {
            throw new TagException(this.tag, "ViewLogsExclude can only be attached to ViewLogs components.");
        }
        String name = getRequiredAttribute("name").getValue();
        ViewLogsComponent viewLogsComponent = (ViewLogsComponent) parent;
        List<String> excludes = viewLogsComponent.getExcludes();
        if (null == excludes) {
            excludes = new LinkedList<>();
            viewLogsComponent.setExcludes(excludes);
        }
        excludes.add(name);
    }
}
