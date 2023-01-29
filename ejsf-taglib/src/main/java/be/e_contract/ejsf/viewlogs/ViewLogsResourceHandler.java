/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.viewlogs;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceHandlerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewLogsResourceHandler extends ResourceHandlerWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ViewLogsResourceHandler.class);

    public static final String LIBRARY_NAME = "view-logs";

    private final ResourceHandler wrappedResourceHandler;

    public ViewLogsResourceHandler(ResourceHandler resourceHandler) {
        //super(resourceHandler); Java EE 8 only
        this.wrappedResourceHandler = resourceHandler;
    }

    @Override
    public ResourceHandler getWrapped() {
        return this.wrappedResourceHandler;
    }

    @Override
    public boolean libraryExists(String libraryName) {
        LOGGER.debug("libraryExists: {}", libraryName);
        if (LIBRARY_NAME.equals(libraryName)) {
            return true;
        }
        return super.libraryExists(libraryName);
    }

    @Override
    public Resource createResource(String resourceName, String libraryName) {
        if (LIBRARY_NAME.equals(libraryName)) {
            LOGGER.debug("resource name: {}", resourceName);
            return new ViewLogsResource(resourceName, libraryName);
        }
        return super.createResource(resourceName, libraryName);
    }
}
