/*
 * Enterprise JSF project.
 *
 * Copyright 2023-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component;

import java.util.ArrayList;
import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.StateHelper;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UINamingContainer;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeDataSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(MemoryInfoComponent.COMPONENT_TYPE)
public class MemoryInfoComponent extends UIComponentBase implements NamingContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryInfoComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.memoryInfo";

    @Override
    public String getFamily() {
        return UINamingContainer.COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        usedMemory,
        maxMemory,
    }

    private void updateMetrics() {
        ArrayList<MBeanServer> mBeanServers = MBeanServerFactory.findMBeanServer(null);
        if (mBeanServers.isEmpty()) {
            return;
        }
        MBeanServer mBeanServer = (MBeanServer) mBeanServers.get(0);
        CompositeDataSupport compositeDataSupport;
        try {
            compositeDataSupport = (CompositeDataSupport) mBeanServer.getAttribute(new ObjectName("java.lang:type=Memory"), "HeapMemoryUsage");
        } catch (MalformedObjectNameException | MBeanException | AttributeNotFoundException | InstanceNotFoundException | ReflectionException ex) {
            return;
        }
        long usedMemory = (Long) compositeDataSupport.get("used");
        long maxMemory = (Long) compositeDataSupport.get("max");
        StateHelper stateHelper = getStateHelper();
        stateHelper.put(PropertyKeys.usedMemory, usedMemory);
        stateHelper.put(PropertyKeys.maxMemory, maxMemory);
    }

    public Long getUsedMemory() {
        StateHelper stateHelper = getStateHelper();
        Long usedMemory = (Long) stateHelper.get(PropertyKeys.usedMemory);
        if (null == usedMemory) {
            updateMetrics();
        }
        return (Long) stateHelper.get(PropertyKeys.usedMemory);
    }

    public Long getMaxMemory() {
        StateHelper stateHelper = getStateHelper();
        Long maxMemory = (Long) stateHelper.get(PropertyKeys.maxMemory);
        if (null == maxMemory) {
            updateMetrics();
        }
        return (Long) stateHelper.get(PropertyKeys.maxMemory);
    }

    public void refresh() {
        LOGGER.debug("refresh");
        updateMetrics();
    }

    public void gc() {
        LOGGER.debug("gc");
        ArrayList<MBeanServer> mBeanServers = MBeanServerFactory.findMBeanServer(null);
        if (mBeanServers.isEmpty()) {
            return;
        }
        MBeanServer mBeanServer = (MBeanServer) mBeanServers.get(0);
        try {
            mBeanServer.invoke(new ObjectName("java.lang:type=Memory"), "gc", null, null);
        } catch (MalformedObjectNameException | InstanceNotFoundException | MBeanException | ReflectionException ex) {
            LOGGER.error("gc error: " + ex.getMessage(), ex);
        }
        updateMetrics();
    }
}
