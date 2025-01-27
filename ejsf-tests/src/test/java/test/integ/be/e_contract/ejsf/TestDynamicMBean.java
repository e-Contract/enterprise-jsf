/*
 * Enterprise JSF project.
 *
 * Copyright 2025 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package test.integ.be.e_contract.ejsf;

import java.util.Date;
import java.util.UUID;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDynamicMBean implements DynamicMBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestDynamicMBean.class);

    @Override
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        LOGGER.debug("getAttribute: {}", attribute);
        if ("productVersion".equals(attribute)) {
            return "7";
        }
        if ("message-count".equals(attribute)) {
            return Long.valueOf(1);
        }
        if ("consumer-count".equals(attribute)) {
            return 2;
        }
        if ("messages-added".equals(attribute)) {
            return Long.valueOf(3);
        }
        if ("delivering-count".equals(attribute)) {
            return 4;
        }
        return attribute;
    }

    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        LOGGER.debug("setAttribute: {}", attribute.getName());
    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        LOGGER.debug("getAttributes");
        return null;
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        LOGGER.debug("setAttributes");
        return null;
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        LOGGER.debug("invoke: {}", actionName);
        if ("listMessages".equals(actionName)) {
            try {
                CompositeData result = new CompositeDataSupport(new CompositeType("message type", "message type description",
                        new String[]{"JMSMessageID", "JMSTimestamp"},
                        new String[]{"JMSMessageID description", "JMSTimestamp description"},
                        new OpenType[]{SimpleType.STRING, SimpleType.LONG}),
                        new String[]{"JMSMessageID", "JMSTimestamp"},
                        new Object[]{UUID.randomUUID().toString(), new Date().getTime()});
                return new CompositeData[]{result};
            } catch (OpenDataException ex) {
                throw new MBeanException(ex);
            }
        }
        if ("moveMessage".equals(actionName)) {
            this.replayedMessageId = (String) params[0];
        }
        return null;
    }

    private String replayedMessageId;

    @Override
    public MBeanInfo getMBeanInfo() {
        LOGGER.debug("getMBeanInfo");
        return new MBeanInfo(TestDynamicMBean.class.getName(), null, null, null, null, null);
    }

    public String getReplayedMessageId() {
        return this.replayedMessageId;
    }
}
