/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.behavior.logger;

import java.io.IOException;
import java.util.Collection;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.BehaviorConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagException;
import javax.faces.view.facelets.TagHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerClientBehaviorTagHandler extends TagHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerClientBehaviorTagHandler.class);

    public LoggerClientBehaviorTagHandler(BehaviorConfig config) {
        super(config);
    }

    @Override
    public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException {
        if (!ComponentHandler.isNew(parent)) {
            return;
        }

        if (!(parent instanceof ClientBehaviorHolder)) {
            throw new TagException(this.tag, "parent must be ClientBehaviorHolder.");
        }

        ClientBehaviorHolder clientBehaviorHolder = (ClientBehaviorHolder) parent;
        Collection<String> eventNames = clientBehaviorHolder.getEventNames();
        if (null == eventNames) {
            return;
        }
        if (eventNames.isEmpty()) {
            return;
        }
        FacesContext facesContext = faceletContext.getFacesContext();
        Application application = facesContext.getApplication();
        LoggerClientBehavior loggerClientBehavior
                = (LoggerClientBehavior) application.createBehavior(LoggerClientBehavior.BEHAVIOR_ID);
        LOGGER.debug("event names: {}", eventNames);
        for (String eventName : eventNames) {
            clientBehaviorHolder.addClientBehavior(eventName, loggerClientBehavior);
        }
    }
}
