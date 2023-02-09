/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostConstructApplicationEventListener implements SystemEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostConstructApplicationEventListener.class);

    @Override
    public void processEvent(SystemEvent event) throws AbortProcessingException {
        String version = Runtime.getVersion();
        LOGGER.info("Enterprise JSF version {}", version);
    }

    @Override
    public boolean isListenerForSource(Object source) {
        return true;
    }
}
