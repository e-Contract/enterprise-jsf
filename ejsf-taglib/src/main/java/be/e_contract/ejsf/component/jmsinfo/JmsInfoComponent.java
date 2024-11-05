/*
 * Enterprise JSF project.
 *
 * Copyright 2023-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.ejsf.component.jmsinfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIOutput;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.DateTimeConverter;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.ListenerFor;
import javax.faces.event.PostAddToViewEvent;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import org.primefaces.component.api.Widget;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesComponent(JmsInfoComponent.COMPONENT_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "ejsf", name = "jms-info.js")
})
@ListenerFor(systemEventClass = PostAddToViewEvent.class)
public class JmsInfoComponent extends UIComponentBase implements NamingContainer, ClientBehaviorHolder, Widget {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsInfoComponent.class);

    public static final String COMPONENT_TYPE = "ejsf.jmsInfo";

    private static final List<String> EVENT_NAMES = Collections.unmodifiableList(Arrays.asList(ReplayEvent.NAME, RemoveEvent.NAME));

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

    private CommandButton replayButton;
    private CommandButton removeButton;
    private UIOutput timestamp;

    public CommandButton getReplayButton() {
        return this.replayButton;
    }

    public void setReplayButton(CommandButton replayButton) {
        this.replayButton = replayButton;
    }

    public CommandButton getRemoveButton() {
        return this.removeButton;
    }

    public void setRemoveButton(CommandButton removeButton) {
        this.removeButton = removeButton;
    }

    public UIOutput getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(UIOutput timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        JmsInfoRenderer jmsInfoRenderer = new JmsInfoRenderer();
        jmsInfoRenderer.encodeBegin(context, this);
        super.encodeBegin(context);
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
        JmsInfoRenderer jmsInfoRenderer = new JmsInfoRenderer();
        jmsInfoRenderer.encodeEnd(context, this);
        super.encodeEnd(context);
    }

    @Override
    public void decode(FacesContext context) {
        JmsInfoRenderer jmsInfoRenderer = new JmsInfoRenderer();
        jmsInfoRenderer.decode(context, this);
        super.decode(context);
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

    public void loadData() {
        loadData(false);
    }

    private boolean isEAP6() {
        ArrayList<MBeanServer> mBeanServers = MBeanServerFactory.findMBeanServer(null);
        if (mBeanServers.isEmpty()) {
            return false;
        }
        MBeanServer mBeanServer = mBeanServers.get(0);
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
            ArrayList<MBeanServer> mBeanServers = MBeanServerFactory.findMBeanServer(null);
            if (mBeanServers.isEmpty()) {
                setError(true);
                return;
            }
            MBeanServer mBeanServer = mBeanServers.get(0);
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

    public boolean isActionsRendered() {
        if (isReplayRendered()) {
            return true;
        }
        if (isRemoveRendered()) {
            return true;
        }
        return false;
    }

    public boolean isReplayRendered() {
        boolean replay = getAttributes().containsKey("replayQueue");
        return replay;
    }

    public boolean isRemoveRendered() {
        Boolean removeAction = (Boolean) getAttributes().get("removeAction");
        if (null == removeAction) {
            return false;
        }
        return removeAction;
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

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public String getDefaultEventName() {
        return ReplayEvent.NAME;
    }

    @Override
    public void processDecodes(final FacesContext fc) {
        if (isSelfRequest(fc)) {
            decode(fc);
        } else {
            super.processDecodes(fc);
        }
    }

    @Override
    public void processValidators(final FacesContext fc) {
        if (!isSelfRequest(fc)) {
            super.processValidators(fc);
        }
    }

    @Override
    public void processUpdates(final FacesContext fc) {
        if (!isSelfRequest(fc)) {
            super.processUpdates(fc);
        }
    }

    private boolean isSelfRequest(FacesContext facesContext) {
        String clientId = getClientId(facesContext);
        ExternalContext externalContext = facesContext.getExternalContext();
        Map<String, String> requestParameterMap = externalContext.getRequestParameterMap();
        String partialSourceParam = requestParameterMap.get(Constants.RequestParams.PARTIAL_SOURCE_PARAM);
        return clientId.equals(partialSourceParam);
    }

    @Override
    public void queueEvent(FacesEvent facesEvent) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (isSelfRequest(facesContext) && facesEvent instanceof AjaxBehaviorEvent) {
            ExternalContext externalContext = facesContext.getExternalContext();
            Map<String, String> requestParameterMap = externalContext.getRequestParameterMap();
            String eventName = requestParameterMap.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = getClientId(facesContext);
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) facesEvent;
            if (ReplayEvent.NAME.equals(eventName)) {
                String messageIdParam = requestParameterMap.get(clientId + "_messageId");
                ReplayEvent replayEvent
                        = new ReplayEvent(this, behaviorEvent.getBehavior(),
                                messageIdParam);
                replayEvent.setPhaseId(facesEvent.getPhaseId());
                super.queueEvent(replayEvent);
                return;
            }
            if (RemoveEvent.NAME.equals(eventName)) {
                String messageIdParam = requestParameterMap.get(clientId + "_messageId");
                RemoveEvent removeEvent
                        = new RemoveEvent(this, behaviorEvent.getBehavior(),
                                messageIdParam);
                removeEvent.setPhaseId(facesEvent.getPhaseId());
                super.queueEvent(removeEvent);
                return;
            }
        }
        super.queueEvent(facesEvent);
    }

    @Override
    public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
        if (event instanceof PostAddToViewEvent) {
            String dateTimePattern = (String) getAttributes().get("dateTimePattern");
            if (null != dateTimePattern) {
                FacesContext facesContext = event.getFacesContext();
                Application application = facesContext.getApplication();
                DateTimeConverter dateTimeConverter = (DateTimeConverter) application.createConverter(DateTimeConverter.CONVERTER_ID);
                dateTimeConverter.setType("both");
                dateTimeConverter.setPattern(dateTimePattern);
                this.timestamp.setConverter(dateTimeConverter);
            }
        }
        super.processEvent(event);
    }
}
