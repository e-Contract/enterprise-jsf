/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.jmsinfo;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(JmsInfoComponent.COMPONENT_TYPE)
public class JmsInfoComponent extends UIComponentBase implements NamingContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsInfoComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.jmsInfo";

    private boolean error;
    private Long messageCount;
    private Integer consumerCount;
    private Long messagesAdded;
    private Integer deliveryCount;
    private String deadLetterAddress;
    private String expiryAddress;
    private String queueAddress;
    private List<JmsMessage> jmsMessages;

    public JmsInfoComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return UINamingContainer.COMPONENT_FAMILY;
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        loadData();
        super.encodeBegin(context);
    }

    private void loadData() {
        String queue = (String) getAttributes().get("queue");
        LOGGER.debug("queue: {}", queue);
        try {
            MBeanServer mBeanServer = (MBeanServer) MBeanServerFactory.findMBeanServer(null).get(0);
            ObjectName queueName = new ObjectName("jboss.as:subsystem=messaging-activemq,server=default,jms-queue=" + queue);
            this.messageCount = (Long) mBeanServer.getAttribute(queueName, "message-count");
            this.consumerCount = (Integer) mBeanServer.getAttribute(queueName, "consumer-count");
            this.messagesAdded = (Long) mBeanServer.getAttribute(queueName, "messages-added");
            this.deliveryCount = (Integer) mBeanServer.getAttribute(queueName, "delivering-count");
            this.deadLetterAddress = (String) mBeanServer.getAttribute(queueName, "deadLetterAddress");
            this.expiryAddress = (String) mBeanServer.getAttribute(queueName, "expiryAddress");
            this.queueAddress = (String) mBeanServer.getAttribute(queueName, "queueAddress");
            this.jmsMessages = new LinkedList<>();
            CompositeData[] messageList = (CompositeData[]) mBeanServer.invoke(queueName, "listMessages", new Object[]{null}, new String[]{String.class.getName()});
            for (CompositeData message : messageList) {
                String jmsMessageId = (String) message.get("JMSMessageID");
                Date jmsTimestamp = new Date((Long) message.get("JMSTimestamp"));
                LOGGER.debug("JMSMessageID: {}", jmsMessageId);
                LOGGER.debug("JMSTimestamp: {}", jmsTimestamp);
                JmsMessage jmsMessage = new JmsMessage(jmsMessageId, jmsTimestamp);
                this.jmsMessages.add(jmsMessage);
            }
        } catch (MalformedObjectNameException | MBeanException | AttributeNotFoundException | InstanceNotFoundException | ReflectionException ex) {
            this.error = true;
            LOGGER.error("error: " + ex.getMessage(), ex);
        }
    }

    public Long getMessageCount() {
        return this.messageCount;
    }

    public Integer getConsumerCount() {
        return this.consumerCount;
    }

    public Long getMessagesAdded() {
        return this.messagesAdded;
    }

    public Integer getDeliveryCount() {
        return this.deliveryCount;
    }

    public String getQueueAddress() {
        return this.queueAddress;
    }

    public String getExpiryAddress() {
        return this.expiryAddress;
    }

    public String getDeadLetterAddress() {
        return this.deadLetterAddress;
    }

    public boolean isError() {
        return this.error;
    }

    public List<JmsMessage> getJmsMessages() {
        return this.jmsMessages;
    }
}
