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
import javax.faces.application.FacesMessage;
import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UINamingContainer;
import javax.faces.context.ExternalContext;
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

    public JmsInfoComponent() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return UINamingContainer.COMPONENT_FAMILY;
    }

    enum PropertyKeys {
        error,
        jmsMessages,
        messageCount,
        consumerCount,
        messagesAdded,
        deliveringCount,
        deadLetterAddress,
        expiryAddress,
        queueAddress
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        loadData();
        super.encodeBegin(context);
    }

    public void setMessageCount(long messageCount) {
        getStateHelper().put(PropertyKeys.messageCount, messageCount);
    }

    public Long getMessageCount() {
        loadData();
        return (Long) getStateHelper().eval(PropertyKeys.messageCount);
    }

    public void setConsumerCount(int consumerCount) {
        getStateHelper().put(PropertyKeys.consumerCount, consumerCount);
    }

    public Integer getConsumerCount() {
        loadData();
        return (Integer) getStateHelper().eval(PropertyKeys.consumerCount);
    }

    public void setMessagesAdded(long messagesAdded) {
        getStateHelper().put(PropertyKeys.messagesAdded, messagesAdded);
    }

    public Long getMessagesAdded() {
        loadData();
        return (Long) getStateHelper().eval(PropertyKeys.messagesAdded);
    }

    public void setDeliveringCount(int deliveryCount) {
        getStateHelper().put(PropertyKeys.deliveringCount, deliveryCount);
    }

    public Integer getDeliveringCount() {
        loadData();
        return (Integer) getStateHelper().eval(PropertyKeys.deliveringCount);
    }

    public void setDeadLetterAddress(String deadLetterAddress) {
        getStateHelper().put(PropertyKeys.deadLetterAddress, deadLetterAddress);
    }

    public String getDeadLetterAddress() {
        loadData();
        return (String) getStateHelper().eval(PropertyKeys.deadLetterAddress);
    }

    public void setExpiryAddress(String expiryAddress) {
        getStateHelper().put(PropertyKeys.expiryAddress, expiryAddress);
    }

    public String getExpiryAddress() {
        loadData();
        return (String) getStateHelper().eval(PropertyKeys.expiryAddress);
    }

    public void setQueueAddress(String queueAddress) {
        getStateHelper().put(PropertyKeys.queueAddress, queueAddress);
    }

    public String getQueueAddress() {
        loadData();
        return (String) getStateHelper().eval(PropertyKeys.queueAddress);
    }

    private void loadData() {
        loadData(false);
    }

    private boolean isEAP6() {
        MBeanServer mBeanServer = (MBeanServer) MBeanServerFactory.findMBeanServer(null).get(0);
        try {
            ObjectName serverName = new ObjectName("jboss.as:management-root=server");
            String productVersion = (String) mBeanServer.getAttribute(serverName, "productVersion");
            LOGGER.debug("product version: {}", productVersion);
            return productVersion.startsWith("6");
        } catch (MalformedObjectNameException | MBeanException | AttributeNotFoundException | InstanceNotFoundException | ReflectionException ex) {
            LOGGER.error("JMX error: " + ex.getMessage(), ex);
            return false;
        }
    }

    private void loadData(boolean force) {
        if (!force) {
            Boolean loaded = (Boolean) getStateHelper().eval(PropertyKeys.error);
            if (null != loaded) {
                return;
            }
        }
        String requiredRole = (String) getAttributes().get("requiredRole");
        if (null != requiredRole) {
            FacesContext facesContext = getFacesContext();
            ExternalContext externalContext = facesContext.getExternalContext();
            if (!externalContext.isUserInRole(requiredRole)) {
                LOGGER.warn("user not in role: {}", requiredRole);
                setError(true);
                return;
            }
        } else {
            LOGGER.warn("requiredRole recommended");
        }
        String queue = (String) getAttributes().get("queue");
        LOGGER.debug("queue: {}", queue);
        String queueNameStr;
        String consumerCountName;
        String messageCountName;
        String messagesAddedName;
        String deliveringCountName;
        if (isEAP6()) {
            queueNameStr = "jboss.as:subsystem=messaging,hornetq-server=default,jms-queue=" + queue;
            consumerCountName = "consumerCount";
            messageCountName = "messageCount";
            messagesAddedName = "messagesAdded";
            deliveringCountName = "deliveringCount";
        } else {
            queueNameStr = "jboss.as:subsystem=messaging-activemq,server=default,jms-queue=" + queue;
            consumerCountName = "consumer-count";
            messageCountName = "message-count";
            messagesAddedName = "messages-added";
            deliveringCountName = "delivering-count";
        }
        try {
            MBeanServer mBeanServer = (MBeanServer) MBeanServerFactory.findMBeanServer(null).get(0);
            ObjectName queueName = new ObjectName(queueNameStr);
            long messageCount = (Long) mBeanServer.getAttribute(queueName, messageCountName);
            setMessageCount(messageCount);
            int consumerCount = (Integer) mBeanServer.getAttribute(queueName, consumerCountName);
            setConsumerCount(consumerCount);
            long messagesAdded = (Long) mBeanServer.getAttribute(queueName, messagesAddedName);
            setMessagesAdded(messagesAdded);
            int deliveringCount = (Integer) mBeanServer.getAttribute(queueName, deliveringCountName);
            setDeliveringCount(deliveringCount);
            String deadLetterAddress = (String) mBeanServer.getAttribute(queueName, "deadLetterAddress");
            setDeadLetterAddress(deadLetterAddress);
            String expiryAddress = (String) mBeanServer.getAttribute(queueName, "expiryAddress");
            setExpiryAddress(expiryAddress);
            String queueAddress = (String) mBeanServer.getAttribute(queueName, "queueAddress");
            setQueueAddress(queueAddress);
            List<JmsMessage> jmsMessages = new LinkedList<>();
            setJmsMessages(jmsMessages);
            CompositeData[] messageList = (CompositeData[]) mBeanServer.invoke(queueName, "listMessages",
                    new Object[]{null},
                    new String[]{String.class.getName()});
            for (CompositeData message : messageList) {
                String jmsMessageId = (String) message.get("JMSMessageID");
                Date jmsTimestamp = new Date((Long) message.get("JMSTimestamp"));
                LOGGER.debug("JMSMessageID: {}", jmsMessageId);
                LOGGER.debug("JMSTimestamp: {}", jmsTimestamp);
                JmsMessage jmsMessage = new JmsMessage(jmsMessageId, jmsTimestamp);
                jmsMessages.add(jmsMessage);
            }
            setError(false);
        } catch (MalformedObjectNameException | MBeanException | AttributeNotFoundException | InstanceNotFoundException | ReflectionException ex) {
            setError(true);
            LOGGER.error("JMX error: " + ex.getMessage(), ex);
        }
    }

    public boolean isError() {
        loadData();
        boolean error = (Boolean) getStateHelper().eval(PropertyKeys.error);
        LOGGER.debug("isError: {}", error);
        return error;
    }

    public void setError(boolean error) {
        getStateHelper().put(PropertyKeys.error, error);
    }

    public List<JmsMessage> getJmsMessages() {
        loadData();
        return (List<JmsMessage>) getStateHelper().eval(PropertyKeys.jmsMessages);
    }

    public void setJmsMessages(List<JmsMessage> jmsMessages) {
        getStateHelper().put(PropertyKeys.jmsMessages, jmsMessages);
    }

    public boolean isReplay() {
        boolean replay = getAttributes().containsKey("replayQueue");
        return replay;
    }

    public void replayMessage(JmsMessage jmsMessage) {
        String queue = (String) getAttributes().get("queue");
        String replayQueue = (String) getAttributes().get("replayQueue");
        String messageId = jmsMessage.getId();
        LOGGER.debug("replay JMS message: {}", messageId);
        String queueNameStr;
        Object[] params;
        String[] signature;
        if (isEAP6()) {
            queueNameStr = "jboss.as:subsystem=messaging,hornetq-server=default,jms-queue=" + queue;
            params = new Object[]{messageId, replayQueue};
            signature = new String[]{String.class.getName(), String.class.getName()};
        } else {
            queueNameStr = "jboss.as:subsystem=messaging-activemq,server=default,jms-queue=" + queue;
            params = new Object[]{messageId, replayQueue, true};
            signature = new String[]{String.class.getName(), String.class.getName(), Boolean.class.getName()};
        }
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            MBeanServer mBeanServer = (MBeanServer) MBeanServerFactory.findMBeanServer(null).get(0);
            ObjectName queueName = new ObjectName(queueNameStr);
            mBeanServer.invoke(queueName, "moveMessage", params, signature);
            facesContext.addMessage(getClientId() + ":jmsMessagesTable",
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Replaying JMS message " + messageId, null));
        } catch (MalformedObjectNameException | MBeanException | InstanceNotFoundException | ReflectionException ex) {
            LOGGER.error("JMX error: " + ex.getMessage(), ex);
            facesContext.addMessage(getClientId() + ":jmsMessagesTable",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "JMX error: " + ex.getMessage(), null));
        }
        loadData(true);
    }

    public void removeMessage(JmsMessage jmsMessage) {
        String queue = (String) getAttributes().get("queue");
        String messageId = jmsMessage.getId();
        LOGGER.debug("remove JMS message: {}", messageId);
        String queueNameStr;
        if (isEAP6()) {
            queueNameStr = "jboss.as:subsystem=messaging,hornetq-server=default,jms-queue=" + queue;
        } else {
            queueNameStr = "jboss.as:subsystem=messaging-activemq,server=default,jms-queue=" + queue;
        }
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            MBeanServer mBeanServer = (MBeanServer) MBeanServerFactory.findMBeanServer(null).get(0);
            ObjectName queueName = new ObjectName(queueNameStr);
            mBeanServer.invoke(queueName, "removeMessage", new Object[]{messageId},
                    new String[]{String.class.getName()});
            facesContext.addMessage(getClientId() + ":jmsMessagesTable",
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Removing JMS message " + messageId, null));
        } catch (MalformedObjectNameException | MBeanException | InstanceNotFoundException | ReflectionException ex) {
            LOGGER.error("JMX error: " + ex.getMessage(), ex);
            facesContext.addMessage(getClientId() + ":jmsMessagesTable",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "JMX error: " + ex.getMessage(), null));
        }
        loadData(true);
    }

    public void refresh() {
        loadData(true);
    }
}
